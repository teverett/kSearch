![CI](https://github.com/teverett/kSearch/workflows/CI/badge.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/9c804e8f75d24a2785450511e57b222c)](https://www.codacy.com/manual/teverett/kSearch?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=teverett/kSearch&amp;utm_campaign=Badge_Grade)

# kSearch

A simple local search engine built on [ElasticSearch](https://www.elastic.co/).  kSearch includes a threaded file system indexer which recursively reads files and indexes them into Elastic.  There is also an automatic updater which deletes files from Elastic which have been deleted from the filesystem.  The automatic updater also reindexes files which have changed on the file system since they were last indexed into Elastic.

## License

kSearch is under the [GPLv3](https://www.gnu.org/licenses/gpl-3.0.html)

## Supported file types

*   Text files including txt,java,c,cpp,sh,pdf,js,json,css,xml,yaml,yml,log,eml,msg,md,htm,html,doc,docx
*   pdf
*   doxc
*   docx

## Configuring

The configuration for kSearch is via a properties file `ksearch.properties`.  The relevant properties are documented in the file.

## Building

To build kSearch you will need:

*   [Java8](https://adoptopenjdk.net/) or better
*   [Maven 3.1.1](https://maven.apache.org/) or better
*   A working install of [Elastic Search](http://https://www.elastic.co/).

To build kSearch

<pre>
mvn clean package
</pre>

## Running
To run kSearch, simply run `run.sh` which will run the binary jar and use `ksearch.properties` as the configuration file.

## Screen shots

<b>Search Screen</b>

<img src="https://github.com/teverett/kSearch/raw/master/screens/index.png" width="800" />

<b>Search Results</b>

<img src="https://github.com/teverett/kSearch/raw/master/screens/results.png" width="800" />


