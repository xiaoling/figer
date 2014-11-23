Fine-Grained Entity Recognizer
=============================

This distribution contains the source code for the experiments presented in the following research publication:

    Xiao Ling and Daniel S. Weld (2012). "Fine-Grained Entity Recognition", in Proceedings OF THE TWENTY-SIXTH AAAI CONFERENCE ON ARTIFICIAL INTELLIGENCE (AAAI), 2012.

[PDF](http://xiaoling.github.com/pubs/ling-aaai12.pdf)

# Testing

One can test the trained model on the evaluation data or new data as they wish. 

Download [Model](https://www.dropbox.com/sh/fg9geomqxhh54qw/AABDS9BZmct9-ku-D0J_v5Dxa/figer.model.gz) and save it at the root directory. 

## Replicate the experiments

To run the experiments in the AAAI-12 paper, you can proceed as follows:

    $ ./run.sh "aaai/exp.conf" &> aaai/exp.log

## On new data

To make predictions on new test data, change the fields `testFile` and
`outputFile` in the file `config/figer.conf` to your desired files. Note
that `testFile` MUST take a file with .txt extension. Then run:

    $ ./run.sh "config/figer.conf" &> figer.log

# Training Data

The training data `train.tar.gz` is serialized in [Protocol Buffer](http://code.google.com/p/protobuf/). Please see `entity.proto` in the code package for the definitions.

Download [link](https://www.dropbox.com/sh/fg9geomqxhh54qw/AAC6LWI4gsnCXuPeQWV5b5yNa/train.tar.gz)

