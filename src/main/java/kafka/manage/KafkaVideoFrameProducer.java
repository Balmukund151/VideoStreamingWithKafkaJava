package kafka.manage;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaVideoFrameProducer {

	private final static String TOPIC = "my-failsafe-topic";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9093,localhost:9094";
	private byte[] videoBuffer;
	private Producer<Long, byte[]> producer;
	private ProducerRecord<Long, byte[]> record;

	public KafkaVideoFrameProducer(byte[] videoBuffer) {
		this.videoBuffer = videoBuffer;
		producer = createProducer();
	}

	private Producer<Long, byte[]> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
		return new KafkaProducer<>(props);
	}

//	static long index = 1;

	public void runProducer() throws InterruptedException {
		try {
			record = new ProducerRecord<Long, byte[]>(TOPIC, videoBuffer);
			producer.send(record);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally {
			producer.flush();
			producer.close();
			System.out.println("Flushed and closed successfully");
		}
	}
}
