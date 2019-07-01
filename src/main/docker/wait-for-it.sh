#!/usr/bin/env bash
#   Use this script to test if a given TCP host/port are available

WAITFORIT_cmdname=${0##*/}
WAITFORIT_EAGER=1

echoerr() { if [[ $WAITFORIT_QUIET -ne 1 ]]; then echo "$@" 1>&2; fi }

usage()
{
    cat << USAGE >&2
Usage:
    $WAITFORIT_cmdname host:port[...,hostN:portN] [-s] [-t timeout] [-- command args]
    -h HOST | --host=HOST       Host or IP under test
    -p PORT | --port=PORT       TCP port under test
                                Alternatively, you specify the host and port as host:port
    -s | --strict               Only execute subcommand if the test succeeds
    -q | --quiet                Don't output any status messages
    -t TIMEOUT | --timeout=TIMEOUT
                                Timeout in seconds, zero for no timeout
    -- COMMAND ARGS             Execute command with args after the test finishes
USAGE
    exit 1
}

wait_for_one(){
    host=$1
    port=$2

    if [[ $WAITFORIT_TIMEOUT -gt 0 ]]; then
        echoerr "$WAITFORIT_cmdname: waiting $WAITFORIT_TIMEOUT seconds for $host:$port"
    else
        echoerr "$WAITFORIT_cmdname: waiting for $host:$port without a timeout"
    fi

    WAITFORIT_start_ts=$(date +%s)

    while :
    do
        if [[ $WAITFORIT_ISBUSY -eq 1 ]]; then
            nc -z $host $port
            WAITFORIT_result=$?
        else
            (echo > /dev/tcp/$host/$port) >/dev/null 2>&1
            WAITFORIT_result=$?
        fi
        if [[ $WAITFORIT_result -eq 0 ]]; then
            WAITFORIT_end_ts=$(date +%s)
            echoerr "$WAITFORIT_cmdname: $host:$port is available after $((WAITFORIT_end_ts - WAITFORIT_start_ts)) seconds"
            break
        fi
        sleep 1
    done

    return $WAITFORIT_result
}

wait_for_list()
{
    WAITFORIT_result=0
    if [[ "$WAITFORIT_URIS" != "" ]]; then
        for address in ${WAITFORIT_URIS//,/ }
        do
            address=${address#*://}
            address=${address#*@}
            address=${address/\/*}
            host_port=(${address//:/ })
            host=${host_port[0]}
            port=${host_port[1]}
            wait_for_one $host $port
            result=$?
            WAITFORIT_result=$((WAITFORIT_result | $result))

            if [[ result -eq 0 && $WAITFORIT_EAGER -eq 0 ]]; then
                WAITFORIT_result=0
                break
            fi
        done
    else
        wait_for_one $WAITFORIT_HOST $WAITFORIT_PORT
        WAITFORIT_result=$?
    fi

    return $WAITFORIT_result
}

wait_for_one_wrapper(){
    host=$1
    port=$2

    # In order to support SIGINT during timeout: http://unix.stackexchange.com/a/57692
    if [[ $WAITFORIT_QUIET -eq 1 ]]; then
        timeout $WAITFORIT_BUSYTIMEFLAG $WAITFORIT_TIMEOUT $0 --quiet --child --host=$host --port=$port --timeout=$WAITFORIT_TIMEOUT &
    else
        timeout $WAITFORIT_BUSYTIMEFLAG $WAITFORIT_TIMEOUT $0 --child --host=$host --port=$port --timeout=$WAITFORIT_TIMEOUT &
    fi
    WAITFORIT_PID=$!
    trap "kill -INT -$WAITFORIT_PID" INT
    wait $WAITFORIT_PID
    WAITFORIT_RESULT=$?
    if [[ $WAITFORIT_RESULT -ne 0 ]]; then
        echoerr "$WAITFORIT_cmdname: timeout occurred after waiting $WAITFORIT_TIMEOUT seconds for $host:$port"
    fi
    return $WAITFORIT_RESULT
}

wait_for_wrapper()
{
    WAITFORIT_result=0
    if [[ "$WAITFORIT_URIS" != "" ]]; then
        for address in ${WAITFORIT_URIS//,/ }
        do
            address=${address#*://}
            address=${address#*@}
            address=${address/\/*}
            host_port=(${address//:/ })
            host=${host_port[0]}
            port=${host_port[1]}
            wait_for_one_wrapper $host $port
            result=$?
            WAITFORIT_result=$((WAITFORIT_result | $result))

            if [[ result -eq 0 && $WAITFORIT_EAGER -eq 0 ]]; then
                WAITFORIT_result=0
                break
            fi
        done
    else
        wait_for_one_wrapper $WAITFORIT_HOST $WAITFORIT_PORT
        WAITFORIT_result=$?
    fi

    return $WAITFORIT_result
}

# process arguments
while [[ $# -gt 0 ]]
do
    case "$1" in
        *:* )
        WAITFORIT_URIS=$1
        shift 1
        ;;
        --child)
        WAITFORIT_CHILD=1
        shift 1
        ;;
        -q | --quiet)
        WAITFORIT_QUIET=1
        shift 1
        ;;
        -s | --strict)
        WAITFORIT_STRICT=1
        shift 1
        ;;
        -ne | --no-eager)
        WAITFORIT_EAGER=0
        shift 1
        ;;
        -h)
        WAITFORIT_HOST="$2"
        if [[ $WAITFORIT_HOST == "" ]]; then break; fi
        shift 2
        ;;
        --host=*)
        WAITFORIT_HOST="${1#*=}"
        shift 1
        ;;
        -p)
        WAITFORIT_PORT="$2"
        if [[ $WAITFORIT_PORT == "" ]]; then break; fi
        shift 2
        ;;
        --port=*)
        WAITFORIT_PORT="${1#*=}"
        shift 1
        ;;
        -t)
        WAITFORIT_TIMEOUT="$2"
        if [[ $WAITFORIT_TIMEOUT == "" ]]; then break; fi
        shift 2
        ;;
        --timeout=*)
        WAITFORIT_TIMEOUT="${1#*=}"
        shift 1
        ;;
        --)
        shift
        WAITFORIT_CLI=("$@")
        break
        ;;
        --help)
        usage
        ;;
        *)
        echoerr "Unknown argument: $1"
        usage
        ;;
    esac
done

if [[ -z "$WAITFORIT_URIS" && ("$WAITFORIT_HOST" == "" || "$WAITFORIT_PORT" == "") ]]; then
    echoerr "Error: you need to provide a host and port to test."
    usage
fi

if [[ $WAITFORIT_URIS == *","*  && $WAITFORIT_TIMEOUT -eq 0 ]]; then
    echoerr "Error: you cannot have timeout 0 when you specify a list"
    exit 1
fi

WAITFORIT_TIMEOUT=${WAITFORIT_TIMEOUT:-15}
WAITFORIT_STRICT=${WAITFORIT_STRICT:-0}
WAITFORIT_CHILD=${WAITFORIT_CHILD:-0}
WAITFORIT_QUIET=${WAITFORIT_QUIET:-0}

# check to see if timeout is from busybox?
WAITFORIT_TIMEOUT_PATH=$(type -p timeout)
WAITFORIT_TIMEOUT_PATH=$(realpath $WAITFORIT_TIMEOUT_PATH 2>/dev/null || readlink -f $WAITFORIT_TIMEOUT_PATH)
if [[ $WAITFORIT_TIMEOUT_PATH =~ "busybox" ]]; then
        WAITFORIT_ISBUSY=1
        WAITFORIT_BUSYTIMEFLAG="-t"

else
        WAITFORIT_ISBUSY=0
        WAITFORIT_BUSYTIMEFLAG=""
fi

if [[ $WAITFORIT_CHILD -gt 0 ]]; then
    wait_for_list
    WAITFORIT_RESULT=$?
    exit $WAITFORIT_RESULT
else
    if [[ $WAITFORIT_TIMEOUT -gt 0 ]]; then
        wait_for_wrapper
        WAITFORIT_RESULT=$?
    else
        wait_for_list
        WAITFORIT_RESULT=$?
    fi
fi

if [[ $WAITFORIT_CLI != "" ]]; then
    if [[ $WAITFORIT_RESULT -ne 0 && $WAITFORIT_STRICT -eq 1 ]]; then
        echoerr "$WAITFORIT_cmdname: strict mode, refusing to execute subprocess"
        exit $WAITFORIT_RESULT
    fi
    exec "${WAITFORIT_CLI[@]}"
else
    exit $WAITFORIT_RESULT
fi
