# Pineapple

Configuration server for use in micro services environment based on gRPC.

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
                    --password=<password> [--private-key=<privateKey>]
                    [--remote=<remote>] --uri=<uri> --username=<username>
                    [-H=<host>] [-P=<port>]
starts the server with specified options
  -H, --host=<host>           the host name that server should listen on
                                Default: localhost
  -P, --port=<port>           the port number that server should listen on
                                Default: 9091
  -S, --ssl                   starts server with SSL/TLS enabled
      --cert=<cert>           certificate chain file
      --private-key=<privateKey>
                              private key file in PEM format
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
Copy and paste gencerts.sh script file on some appropriate directory (exp. ~/certs), then:
```
$ chmod 777 gencerts.sh
$ ./gencerts.sh
```
NOTE: Change the password value on gencerts.sh!

Now start the server with options:
```
$ ./pineapple -S --cert ~/certs/server.crt --private-key ~/certs/server.pem ... (other options)
```
