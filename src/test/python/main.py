import datetime
import random
import sys
import threading

import requests
import pika


class Consumer(threading.Thread):
    num_of_ticks = 0

    def __init__(self, name, schedule):
        threading.Thread.__init__(self)
        self.name = name
        self.schedule = schedule

    def run(self):
        self.num_of_ticks = 0
        clock = self.create_clock()
        self.listen_on_ticks(clock.channel)
        print("Name: %s | schedule: %s" % (self.name, self.schedule))
        sys.stdout.flush()

    def create_clock(self):
        response = requests.post("https://ticktok-io-dev.herokuapp.com/api/v1/clocks")
        assert response.status_code == 201
        return response.json()["channel"]

    def listen_on_ticks(self, clock_channel):
        connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        channel = connection.channel()
        channel.basic_consume(self.tick_callback, queue=clock_channel['queue'], no_ack=True)
        channel.start_consuming()

    def tick_callback(self, ch, method, properties, body):
        self.num_of_ticks += 1
        print("%s, %s, %s" % (str(datetime.datetime.now()), self.name, self.schedule))
        ch.stop_consuming()


class TicktokTester(object):
    consumers = []

    def test(self, num_of_clocks, consumers_per_clock):
        for clockIdx in range(0, num_of_clocks):
            schedule = self.draw_schedule()
            for consumerIdx in range(0, consumers_per_clock):
                self.invoke_consumer_for("test-consumer-%s" % consumerIdx, schedule)
        self.wait_for_consumer_to_finish()

    def draw_schedule(self):
        secs = random.randint(5, 11)
        return "every.%s.seconds" % secs

    def invoke_consumer_for(self, name, schedule):
        consumer = Consumer(name, schedule)
        consumer.start()
        self.consumers.append(consumer)

    def wait_for_consumer_to_finish(self):
        for c in self.consumers:
            c.join()


if __name__ == '__main__':
    TicktokTester().test(5, 10)
