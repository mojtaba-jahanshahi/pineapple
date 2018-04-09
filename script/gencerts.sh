# Change these to match your hosts in your environment if needed.
SERVER_CN=localhost
CLIENT_CN=localhost # Used when doing mutual TLS with gRPC.
PASSWORD=password # Change this to customize certificates generation.

echo Generate CA key:
openssl genrsa -passout pass:${PASSWORD} -des3 -out ca.key 4096
echo Generate CA certificate:
openssl req -passin pass:${PASSWORD} -new -x509 -days 365 -key ca.key -out ca.crt -subj "/CN=${SERVER_CN}"

echo Generate server key:
openssl genrsa -passout pass:${PASSWORD} -des3 -out server.key 4096
echo Generate server signing request:
openssl req -passin pass:${PASSWORD} -new -key server.key -out server.csr -subj "/CN=${SERVER_CN}"
echo Self-signed server certificate:
openssl x509 -req -passin pass:${PASSWORD} -days 365 -in server.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out server.crt
echo Remove pass-phrase from server key:
openssl rsa -passin pass:${PASSWORD} -in server.key -out server.key

echo Generate client key
openssl genrsa -passout pass:${PASSWORD} -des3 -out client.key 4096
echo Generate client signing request:
openssl req -passin pass:${PASSWORD} -new -key client.key -out client.csr -subj "/CN=${CLIENT_CN}"
echo Self-signed client certificate:
openssl x509 -passin pass:${PASSWORD} -req -days 365 -in client.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out client.crt
echo Remove pass-phrase from client key:
openssl rsa -passin pass:${PASSWORD} -in client.key -out client.key

echo Converting the private keys to X.509:
openssl pkcs8 -topk8 -nocrypt -in server.key -out server.pem
openssl pkcs8 -topk8 -nocrypt -in client.key -out client.pem
