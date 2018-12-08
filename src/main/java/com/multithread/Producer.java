package com.multithread;

import java.util.Random;
import java.util.function.IntConsumer;

public class Producer implements Runnable {

	private final Random random = new Random();
	private final IntConsumer consumer;

	public Producer(final IntConsumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public void run() {
		try {
			while (Thread.currentThread().isInterrupted() == false) {
				final int next = random.nextInt(5) + 1;
				consumer.accept(next);
				Thread.sleep(100);
			}
		} catch (final Exception ex) {
		}
	}

}
