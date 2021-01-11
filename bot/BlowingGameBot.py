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
        self.__url = "http://localhost/?game=blow&player=" + self.__uuid
        #self.__url = "http://dva232-project-group-7.atwebpages.com/?game=blow&player=" + self.__uuid
        self.__pinger = Pinger("blow", self.__uuid)
        self.__limit = None
        self.__field = None

    def run(self) -> None:
        r = requests.get(self.__url + "&action=add_queue")
        print(r.text)
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

        while 0 < self.__field < 200:
            while True:
                move = random.randint(0, 20)
                r = requests.get(self.__url + "&action=add_move&move=" + str(move))
                if "waiting for opponent move" == r.json()["response"]:
                    break
                try:
                    val = int(r.json()["response"])
                    self.__field = val
                    break
                except ValueError as e:
                    print("set move", e, r.text)
            while True: 
                r = requests.get(self.__url + "&action=get_move")
                try:
                    val = int(r.json()["response"])
                    self.__field = val
                    break
                except ValueError as e:
                    print("get move",e, r.text)

        print("in the end the field is:", self.__field)
        if (self.__limit == 200 and self.__field >= self.__limit) or (self.__limit == 0 and self.__field <= self.__limit):
            print("winner")
        else:
            print("looser")
