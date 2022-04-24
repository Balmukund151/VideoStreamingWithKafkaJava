package show.video;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import kafka.manage.KafkaVideoFrameConsumer;

public class VideoReceiverController {
	
	@FXML
	private ImageView view;
	private byte[] payload;
	private ScheduledExecutorService timer;
	public static boolean isDisplayActive=true;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public VideoReceiverController() {
		KafkaVideoFrameConsumer consumer = new KafkaVideoFrameConsumer();		
		Runnable frameGrabber = new Runnable() {

			@Override
			public void run() {
				if(!isDisplayActive) {
					stopDisplay();
				}
				payload = consumer.fetchImage();	
				if(payload!=null) {
					ByteArrayInputStream is = new ByteArrayInputStream(payload);
			        BufferedImage imageToShow = null;
					try {
						imageToShow = ImageIO.read(is);
					} catch (IOException e) {
						e.printStackTrace();
					}
					updateImageView(view, javafx.embed.swing.SwingFXUtils.toFXImage(imageToShow, null));
				}				
			}
		};

		this.timer = Executors.newSingleThreadScheduledExecutor();
		this.timer.scheduleAtFixedRate(frameGrabber, 0, 35, TimeUnit.MILLISECONDS);
	}
	
	public void stopDisplay() {
		if (this.timer != null && !this.timer.isShutdown()) {
			try {
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(35, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 * 
	 * @param view  the {@link ImageView} to update
	 * @param imageToShow the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image imageToShow) {
		Utils.onFXThread(view.imageProperty(), imageToShow);
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	

}
