package kafka.manage;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaVideoFrameConsumer {
	private String bootstrapServers = "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094";
	private String groupId = "video-stream-topic";
	private String topic = "my-failsafe-topic";
	private KafkaConsumer<Long, byte[]> consumer;

	public KafkaVideoFrameConsumer() {
		createVideoConsumer();
	}

	public void createVideoConsumer() {
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		consumer = new KafkaConsumer<Long, byte[]>(properties);
		consumer.subscribe(Arrays.asList(topic));
	}

	public byte[] fetchImage() {
		ConsumerRecords<Long, byte[]> records = consumer.poll(Duration.ofMillis(100));
		for (ConsumerRecord<Long, byte[]> record : records) {
			if (!records.isEmpty()) {
				return (byte[]) record.value();
			}
		}
		return null;
	}
}
