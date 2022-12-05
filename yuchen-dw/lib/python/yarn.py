#!/usr/bin/python2
# -*- coding: utf-8 -*-

from __future__ import print_function
import os
import re
import sys
import signal
import shlex
import argparse

from subprocess import Popen, PIPE
from datetime import datetime
from operator import itemgetter

YARN_APP_LIST_CMD = "yarn application -appStates {} -list"
YARN_APP_LIST_HEAD_DEFAULT = 1000
YARN_APP_LIST_KEYS = [
    "id",
    "name",
    "type",
    "user",
    "queue",
    "state",
    "final-state",
    "progress",
    "url",
]
YARN_APP_STATUS_CMD = "yarn application -status"
YARN_APP_STATUS_ASC_ORDER = False
YARN_APP_STATUS_VALUES = [
    "ALL",
    "NEW",
    "NEW_SAVING",
    "SUBMITTED",
    "ACCEPTED",
    "RUNNING",
    "FINISHED",
    "FAILED",
    "KILLED",
]
YARN_APP_STATUS_DEFAULT = ["RUNNING"]
TERM_WIDTH = 141
TERM_HEADERS = ["ID", "Name", "State", "User", "Type", "Start", "Duration"]
TERM_HEAD_TEMPLATE = "| {:<30} | {:25} | {:<10} | {:<10} | {:<10} | {:<19} | {:<15} |"
TERM_BODY_TEMPLATE = "| {id:<30} | {name:25} | {state:<10} | {user:<10} | {type:<10} | {startfmt:<19} | {duration:<15} |"


def check_int(number, positive=True):
    try:
        number = int(number)
    except Exception as err:
        raise Exception("value: {0} not integer. error: [{1}]".format(number, err))
    if positive and number < 0:
        raise Exception("negative integer")
    return number


def exec_cmd(cmd, wait=True, logger=None, timeout=0):
    if timeout:
        def timeout_handler(sign, _):
            os.killpg(ejecucion_cmd.pid, signal.SIGKILL)
            exit_code = ejecucion_cmd.wait()

            raise Exception(
                "excedido tiempo ({0} seg) en la ejecución de cmd: {1}. "
                "sign: {2:d}. exit code: {3:d}".format(timeout, cmd, sign, exit_code)
            )
        signal.signal(signal.SIGALRM, timeout_handler)

    ejecucion_cmd = Popen(
        shlex.split(cmd), stdout=PIPE, stderr=PIPE, preexec_fn=os.setsid
    )

    if logger:
        logger.debug(
            "pid: {0} cmd: {1} timeout: {2}".format(
                ejecucion_cmd.pid, re.sub(r"\n|\s+", " ", cmd), timeout
            )
        )

    if wait:
        signal.alarm(timeout)
        out, err = ejecucion_cmd.communicate()
    else:
        out = None
        err = None

    signal.alarm(0)

    return {
        "out": re.sub(r"\r", "", out.strip()) if out is not None else "",
        "err": err,
        "cmd": cmd,
        "exitCode": ejecucion_cmd.wait(),
    }


def get_metadata(statuses, head=None, sort_asc=None):
    if head is None:
        head = YARN_APP_LIST_HEAD_DEFAULT

    if sort_asc is None:
        sort_asc = YARN_APP_STATUS_ASC_ORDER

    apps_values = [
        re.split(r"\s*\t\s*", app_details)
        for status in statuses
        for app_details in exec_cmd(YARN_APP_LIST_CMD.format(status))["out"].split(
            "\n"
        )[2:]
    ][:head]

    apps_meta = [
        dict(zip(YARN_APP_LIST_KEYS, app_values)) for app_values in apps_values
    ]

    for app_meta in apps_meta:
        status_dict = {
            fields[0]: fields[1]
            for fields in [
                re.sub(r"\s+", "", field).strip().split(":")
                for field in exec_cmd(
                    "{} {}".format(YARN_APP_STATUS_CMD, app_meta["id"])
                )["out"].split("\n")[2:17]
            ]
        }

        status_dict["Start-Formatted-Time"] = datetime.fromtimestamp(
            int(status_dict["Start-Time"]) / 1000
        ).strftime("%Y-%m-%d %H:%M:%S")

        status_dict["Finish-Formatted-Time"] = (
            datetime.fromtimestamp(int(status_dict["Finish-Time"]) / 1000).strftime(
                "%Y-%m-%d %H:%M:%S"
            )
            if status_dict["Finish-Time"] != "0"
            else "0"
        )

        app_meta["status"] = status_dict
        app_meta["start"] = int(status_dict["Start-Time"])
        app_meta["finish"] = int(status_dict["Finish-Time"])

        now = (
            datetime.fromtimestamp(int(status_dict["Finish-Time"]) / 1000)
            if app_meta["finish"] != 0
            else datetime.now()
        )

        app_meta["duration"] = str(
            now - datetime.fromtimestamp(int(status_dict["Start-Time"]) / 1000)
        ).split(".")[0]
        app_meta["startfmt"] = status_dict["Start-Formatted-Time"]

    return sorted(apps_meta, key=itemgetter("start"), reverse=not sort_asc)


def get_params(argv):
    parser = argparse.ArgumentParser(
        prefix_chars="-+",
        add_help=True,
        description="yarn extended",
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )

    parser.add_argument(
        "--status",
        action="append",
        dest="status",
        help="app status [default ALL]",
        choices=YARN_APP_STATUS_VALUES,
    )

    parser.add_argument(
        "--head",
        dest="head",
        action="store",
        type=check_int,
        help="first n rows [default {}]".format(YARN_APP_LIST_HEAD_DEFAULT),
        default=YARN_APP_LIST_HEAD_DEFAULT,
    )

    parser.add_argument(
        "--asc",
        action="store_true",
        dest="asc",
        default=YARN_APP_STATUS_ASC_ORDER,
        help="start time in ASC order [Default {}]".format(
            str(YARN_APP_STATUS_ASC_ORDER)
        ),
    )

    return parser


def ui(apps):
    print("-" * TERM_WIDTH)
    print(TERM_HEAD_TEMPLATE.format(*TERM_HEADERS))
    print("-" * TERM_WIDTH)
    for app in apps:
        print(TERM_BODY_TEMPLATE.format(**app))
    print("-" * TERM_WIDTH)

    return None


if __name__ == "__main__":
    sys_args = sys.argv[1:]

    parser = get_params(sys_args)
    args = parser.parse_args(sys_args)

    ui(
        get_metadata(
            args.status if args.status is not None else YARN_APP_STATUS_DEFAULT,
            head=args.head,
            sort_asc=args.asc,
        )
    )

    sys.exit(0)
