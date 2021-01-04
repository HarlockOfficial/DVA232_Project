import os
import sys
import time
from threading import Thread

import requests


class Pinger(Thread):
    def __init__(self, game_code: str, uuid: str):
        super().__init__()
        self.__url = "https://dva232-project-group-7.000webhostapp.com/ping.php?game=" + game_code + \
                     "&player=" + uuid
        self.__playing = True

    def run(self) -> None:
        while self.__playing:
            r = requests.get(self.__url)
            if r.json()["response"] != "ok":
                print(r.text)
                os._exit(1)
            time.sleep(2)

    def stop(self):
        self.__playing = False
