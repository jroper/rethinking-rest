# Rethinking REST in a microservices world

This is repo contains all the resources for my Rethinking REST in a microservices world presentation.

This (master) branch contains the following:

* `rethinking-rest.pdf`, a PDF version of the presentation, with all animations expanded to individual slides
* `rethinking-rest.odt`, the original LibreOffice presentation
* `RethinkingRest.xml`, the IntelliJ Live Templates that I use to do the live coding.

The source code for the presentation can be found in the `initial` branch, it is based on https://github.com/lagom/activator-lagom-java-chirper with a few additions including the like service, and some refactorings.  It depends on a few bug fixes that can be found in the master branch of Lagom, so you'll need to clone and run `publishLocal` on Lagom itself for it to work properly.  The completed source code can be found in the `complete` branch.

