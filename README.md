Fine-Grained Entity Recognizer (FIGER)
=============================

This distribution contains the source code for the experiments presented in the following research publication ([PDF](http://xiaoling.github.com/pubs/ling-aaai12.pdf)):

    Xiao Ling and Daniel S. Weld (2012). 
    "Fine-Grained Entity Recognition", 
    in Proceedings OF THE TWENTY-SIXTH AAAI CONFERENCE ON ARTIFICIAL INTELLIGENCE (AAAI), 2012. 

## Download the model file

One can test the trained model on the evaluation data or new data as they wish. 

Run `./downloadModel.sh` to download the [Model](https://drive.google.com/open?id=0B52yRXcdpG6MWlVXaTFXWVZQYjg) and save it at the root directory. 

A better [model](https://drive.google.com/open?id=0B52yRXcdpG6Mbm1TdHhYdVBmSnM) has been trained and can be fetched by `./downloadModel.sh new`. Change the config value accordingly.

## Requirement

sbt >= 0.13.0

## Replicate the experiments

To run the experiments in the AAAI-12 paper, you can proceed as follows:

    $ ./run.sh "aaai/exp.conf" &> aaai/exp.log

## Run FIGER on new data

To make predictions on new data, please see `package edu.washington.cs.figer.FigerSystem` for example code or run:

    $ sbt "runMain edu.washington.cs.figer.FigerSystem <text_file>"

Alternatively, you can change the parameter values (e.g. the input file name) in `config/figer.conf` and get a more structured output by running:
    
    $ ./run.sh "config/figer.conf"

## Make a stand-alone jar

    $ sbt assembly

## A simple web interface

Run 

    $ sbt ~container:start

and go to `localhost:8080/index.html` for a simple web demo.

## Training Data

The training data `train.data.gz` is gzipped and serialized in [Protocol Buffer](http://code.google.com/p/protobuf/). Please see `entity.proto` in the code package for the definitions.

Download [link](https://drive.google.com/open?id=0B52yRXcdpG6MMnRNV3dTdGdYQ2M)

In `config/figer.conf`, make the following changes:
    
    useModel=false
    modelFile=<the output model file>
    
    trainFile=<training file> # the training file has to follow the specs from `entity.proto`. See `train.data.gz` for example

Then run `./run.sh config/figer.conf` to train a new model (It will need over 10G memory and about an hour to finish).
