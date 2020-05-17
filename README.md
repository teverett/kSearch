[![Travis Status](https://api.travis-ci.com/teverett/kSearch.svg?branch=master)](https://travis-ci.com/teverett/kSearch.svg)

# kSearch

A simple local search engine built on [ElasticSearch](https://www.elastic.co/)

## License

kSearch is under the [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html)

## Supported file types

* Text
* PDF
* DOC
* DOCX

## Configuring

The configuration for kSearch is via a properties file `ksearch.properties`.  The relevant properties are documented in the file.

## Building

To build kSearch you will need:

* [Java8](https://adoptopenjdk.net/) or better
* [Maven 3.1.1](https://maven.apache.org/) or better
* A working install of [Elastic Search](http://https://www.elastic.co/).

To build kSearch

<pre>
mvn clean package
</pre>

## Running

To run kSearch, simply run `run.sh` which will run the binary jar and use `ksearch.properties` as the configuration file.

