package com.taobao.test.activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQTest2 {
	public static void main(String [] args) throws InterruptedException{
		thread(new Producer(),false);

		thread(new Consumer(),false);
		//Thread.sleep(2000);
	}
	public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
}

class Producer implements Runnable{
	private static final int SEND_NUMBER = 5;
	@Override
	public void run() {
		ConnectionFactory connFactory;
		Connection conn = null;
		Session session;
		Destination destination;
		MessageProducer msgProducer;
		
		//构造ConnectionFactory实例对象
		connFactory = new ActiveMQConnectionFactory(
						ActiveMQConnection.DEFAULT_USER,
						ActiveMQConnection.DEFAULT_PASSWORD,
						"tcp://localhost:61616"
						);
		try{
			//从构造工厂获得连接对象
			conn = connFactory.createConnection();
			//启动连接
			conn.start();
			//获得连接session
			session = conn.createSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
			//获得连接目的地
			destination = session.createQueue("villasy-queue");
			//消息生产者
			msgProducer = session.createProducer(destination);
			//设置不持久化
			msgProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			//构造并发送消息
			sendMsg(session,msgProducer);
			session.commit();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(null != conn)
				try {
					conn.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void sendMsg(Session session,MessageProducer msgProducer) throws JMSException{
		for(int i=1;i<SEND_NUMBER;i++){
			TextMessage txtMsg = session.createTextMessage("发送消息:"+i);
			System.out.println(txtMsg.getText());
			msgProducer.send(txtMsg);
		}
	}
	
}
class Consumer implements Runnable,javax.jms.ExceptionListener{

	@Override
	public void run() {
		// ConnectionFactory ：连接工厂，JMS 用它创建连接
		ConnectionFactory connectionFactory;
		// Connection ：JMS 客户端到JMS Provider 的连接
		Connection connection = null;
		// Session： 一个发送或接收消息的线程
		Session session;
		// Destination ：消息的目的地;消息发送给谁.
		Destination destination;
		// 消费者，消息接收者
		MessageConsumer consumer;

		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD,
				"tcp://localhost:61616");
		try {
			// 构造从工厂得到连接对象
			connection = connectionFactory.createConnection();
			// 启动
			connection.start();
			// 获取操作连接
			session = connection.createSession(Boolean.FALSE,
								Session.AUTO_ACKNOWLEDGE);
			// 获取session0的queue
			destination = session.createQueue("villasy-queue");
			consumer = session.createConsumer(destination);
			
			while (true) {
				TextMessage message = (TextMessage) consumer.receive(1000);
				if (null != message) {
					System.out.println("收到消息" + message.getText());
				} else {
				break;
				}	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != connection)
					connection.close();
			} catch (Throwable ignore) {
		}
	  }
	}

	@Override
	public synchronized void onException(JMSException arg0) {
		System.out.println("JMS Exception occured.  Shutting down client.");
	}
	
}