package show.video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import kafka.manage.KafkaVideoFrameProducer;

public class VideoController {

	@FXML
	private Button button;
	@FXML
	private ImageView view;

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	private ScheduledExecutorService timer;
	private VideoCapture capture = new VideoCapture();
	public static boolean cameraActive = false;
	private static int cameraId = 0;
	private KafkaVideoFrameProducer producer;

	@FXML
	protected void startCamera(ActionEvent event) {
		if (!cameraActive) {
			this.capture.open(cameraId);
			if (this.capture.isOpened()) {
				cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {
						if(!cameraActive) {
							stopAcquisition();
						}
						Mat frame = grabFrame();
						BufferedImage imageToShow = Utils.matToBufferedImage(frame);
						updateImageView(view, SwingFXUtils.toFXImage(imageToShow, null));
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try {
							ImageIO.write(imageToShow, "jpg", baos);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						byte[] inputBuff = baos.toByteArray();
						producer = new KafkaVideoFrameProducer(inputBuff);
						try {
							producer.runProducer();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				this.button.setText("Stop Camera");
			} else {
				System.err.println("camera still closed.");
			}
		} else {
			cameraActive = false;
			this.button.setText("Start Camera");

			// stop the timer
			this.stopAcquisition();
		}
	}

	private Mat grabFrame() {
		// init everything
		Mat frame = new Mat();

		if (this.capture.isOpened()) {
			try {
				this.capture.read(frame);
				if (!frame.empty()) {
					Imgproc.cvtColor(frame, frame, Imgproc.COLOR_GRAY2RGB);
				}
			} catch (Exception e) {
				// log the error
				e.printStackTrace();
			}
		}
		return frame;
	}

	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	public void stopAcquisition() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		if (this.capture.isOpened()) {
			// release the camera
			this.capture.release();
		}
	}

	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view  the {@link ImageView} to update
	 * @param image the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image) {
		Utils.onFXThread(view.imageProperty(), image);
	}

	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed() {
		this.stopAcquisition();
	}

}
