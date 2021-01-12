import os
import sys
import time
from threading import Thread

import requests


class Pinger(Thread):
    def __init__(self, game_code: str, uuid: str):
        super().__init__()
        self.__url = "http://localhost/ping.php?game=" + game_code + "&player=" + uuid
        #self.__url = "http://dva232-project-group-7.atwebpages.com/ping.php?game=" + game_code + \
                     #"&player=" + uuid
        self.__playing = True

    def run(self) -> None:
        while self.__playing:
            r = requests.get(self.__url)
            sys.stderr.write("pingsent")
            if r.json()["response"] != "ok":
                print("Enemy ping stopped, exiting")
                os._exit(1)
            time.sleep(3)

    def stop(self):
        self.__playing = False
