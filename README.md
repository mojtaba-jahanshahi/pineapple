# Pineapple

Pineapple is a configuration server used in micro services environments.

## Features
    - Reads configurations from remote git repositories.
    - Support multiple applications/multiple profiles.
    - Reloads configurations without restarting the server.
    - Automatic scheduled upstream change check.
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
Usage: <main class> [-h] [--branch=<branch>] --password=<password>
                    [--remote=<remote>] --uri=<uri> --username=<username>
starts the server with specified options
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
Copy and paste script/gencerts.sh script file on some appropriate directory (exp. ~/certs), then:
```
$ ./gencerts.sh
```

NOTE: Change the password value on gencerts.sh!

You can also use your own certificate files, for example generated let'sencrypt certificate for your domain in PEM format.

Now start the server with options:
```
$ ./pineapple -S --cert ~/certs/server.crt --private-key ~/certs/server.pem
```
