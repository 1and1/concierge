[![Travis CI](https://travis-ci.org/1and1/concierge.svg?branch=master)](https://travis-ci.org/1and1/concierge) 
[![Codacy Badge](https://api.codacy.com/project/badge/grade/bc3bc8ae53944d9e991b071a270f4b0c)](https://www.codacy.com/app/1and1_NDev/concierge) 
[![Codacy Badge](https://api.codacy.com/project/badge/coverage/bc3bc8ae53944d9e991b071a270f4b0c)](https://www.codacy.com/app/1and1_NDev/concierge)

Concierge
============

The intended usage of the _Concierge_ framework is for having a REST API gateway for JSON based resources.
The idea arised when we we're facing the problem of creating an API for a large XML that we would like to parse with [FraLaX](https://github.com/1and1/fralax).

Once we'll have a running service we'll update the usage guidelines and the documentation for this repository.

## Example

In order to run the example execute 'gradle run' within the module 'concierge-example-server' and call 
[http://localhost:8080/](http://localhost:8080/) with your favored REST client (e.g. _Postman_).