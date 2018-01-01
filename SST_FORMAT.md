# SST Font Format
SST is the writing system for Vo'Zaken. This document describes the file
format for SST fonts. The specifics of the language and writing system are
not covered in this document.

## Overview
An SST font consists mainly of two components: images of each character, and
information about the location of the writing spine for each vowel line and
consonant.

An SST font file holds this information within a ZIP archive. The font's
metadata, as well as writing spine information, is held within a `config`
file in the archive root. All image files are also in this root, named after
the represented character. A default image file should also be included, 
to be used anywhere an image is missing from the font file.

## File Structure
```
MyFont.sst                    ZIP Archive
|- config                     Text File
|- aa.*                       Image File
|- p.*                        Image File
|- leanmark.*                 Image File
|- ...
|- default.*                  Image File
```

The order of files does not matter.

## `config` File
The `config` file contains font metadata and spine data. It is a UTF-8
encoded text file with a newline-separated list of key=value settings.

Example `config`:
```
name=My Font
line-height=XX
a=XX,YY;XX,YY;XX,YY
aa=XX,YY;XX,YY;XX,YY;XX,YY
...
p=XX
b=XX
...
```
The spine data for consonants is straightforward, and is the row of
horizontal pixels of the consonant's image corresponding to the spine
(0 is the top of the image).

Vowel spine data can be more complicated than one horizontal line. In this
font format, vowel spines are represented as a sequence of points (x, y)
which, when connected sequentially by straight lines, forms a continuous
writing spine across the entire width of the vowel. x=0 is the left of the
vowel image, while again y=0 is the top of the image.

If no point has an x greater than the image width, an implied point is added
with the same y value as the last point within the sequence. The same is not
applied to the left bound -- a point with x=0 should always be provided!

Points should be listed by order of x value, separated by semicolons.