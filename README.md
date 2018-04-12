# Pineapple

A configuration server used in micro services environment.

## Features
    - Reads configurations from remote git repositories.
    - Supports multiple applications.
    - Reloads configurations without restarting the server with automatic scheduled upstream checking.
    - SSL/TLS support.

## Building the project
```
$ gradle clean installDist
```

## Usage help
Then under the build/install/pineapple/bin:
```
$ ./pineapple --help
```

```
Usage: <main class> [-hS] [--branch=<branch>] [--cert=<cert>]
                    --password=<password> [--private-key=<pKey>]
                    [--remote=<remote>] --uri=<uri> --username=<username>
                    [-H=<host>] [-P=<port>]
starts the server with specified options
  -H, --host=<host>           the host name that server should listen on
                                Default: localhost
  -P, --port=<port>           the port number that server should listen on
                                Default: 9091
  -S, --ssl                   starting server with SSL/TLS enabled
      --cert=<cert>           certificate chain file in PEM format
      --private-key=<pKey>    private key file in PEM format
*     --uri=<uri>             http(s) address of remote git repository
      --remote=<remote>       remote name to keep track of upstream repository
                                Default: origin
      --branch=<branch>       branch name to read configuration files from
                                Default: master
*     --username=<username>   username that has access to remote git repository
*     --password=<password>   the password of provided username
  -h, --help                  display usage help and exit
```

## SSL/TLS setup
Copy and paste script/gencert.sh script file on some appropriate directory (exp. ~/certs), then:
```
$ chmod 777 gencert.sh
$ ./gencert.sh
```
NOTE: Change the password value on gencert.sh!

You can also use your own certificate files, for example generated let'sencrypt certificate for your domain in PEM format.

Now start the server with options:
```
$ ./pineapple -S --cert ~/certs/server.crt --private-key ~/certs/server.pem ... (other options)
```
