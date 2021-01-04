import os
from threading import Thread
import requests
from string import ascii_letters, digits
import time
import json
import random

from Pinger import Pinger


class BlowingGameBot(Thread):
    def __init__(self):
        super().__init__()
        self.__uuid = "bot_" + "".join(random.choices(ascii_letters + digits + "_", k=14))
        self.__url = "https://dva232-project-group-7.000webhostapp.com/?game=blow&player=" + self.__uuid
        self.__pinger = Pinger("blow", self.__uuid)
        self.__limit = None
        self.__field = None

    def run(self) -> None:
        r = requests.get(self.__url + "&action=add_queue")
        self.__pinger.start()
        if r.json()['response'] == "in_queue":
            print("Blowing Game Bot In Queue")
            while True:
                r = requests.get(self.__url + "&action=get_queue")
                if r is not None and r.json()['response'] != "in_queue":
                    break
                time.sleep(0.01)
        print("Blowing Game Bot playing")
        ret = json.loads(r.json()['response'])
        if ret['starting_player'] == self.__uuid:
            self.__limit = 200
        else:
            self.__limit = 0
        print("bot must reach", self.__limit, "to win")
        self.__field = int(ret['field'])
        self.__set_move()

    def __set_move(self):
        print(self.__field)
        move = random.randint(0, 100)
        r = requests.get(self.__url + "&action=add_move&move=" + str(move))
        ret = r.json()['response']
        try:
            self.__field = int(ret)
            if self.__field >= 200 or self.__field <= 0:
                print("in the end the field is:", self.__field)
                if (self.__limit == 200 and self.__field >= self.__limit) \
                        or (self.__limit == 0 and self.__field <= self.__limit):
                    print("winner")
                else:
                    print("looser")
                os._exit(0)
            self.__get_move()
        except ValueError:
            self.__get_move()

    def __get_move(self):
        time.sleep(0.5)
        r = requests.get(self.__url + "&action=get_move")
        ret = r.json()['response']
        try:
            self.__field = int(ret)
            if self.__field >= 200 or self.__field <= 0:
                print("in the end the field is:", self.__field)
                if (self.__limit == 200 and self.__field >= self.__limit) \
                        or (self.__limit == 0 and self.__field <= self.__limit):
                    print("winner")
                else:
                    print("looser")
                os._exit(0)
            self.__set_move()
        except ValueError:
            self.__get_move()
