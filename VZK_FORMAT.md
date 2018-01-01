# .vzk File Format Documentation
A file with the .vzk extension is used to digitally save Vo'Zaken text.
This document specifies both the encoding used for the digital representation
of characters, as well as the format of .vzk files.

## Encoding
The tables below match codepoints to their corresponding character, broken
down by type of character. Only 0x00 - 0x5F have been allocated.
0x60 - 0xFF have been left unallocated for future expansion. In addition,
each character type range has multiple spaces also available for future
expansion within that character type.

0x00 - 0x0F | Whitespace
--- | ---
0x0a | LineAlternation

0x10 - 0x1F | Digits
--- | ---
0x10 | z0
0x11 | z1
0x12 | z2
0x13 | z3
0x14 | z4
0x15 | z5
0x16 | z6
0x17 | z7
0x18 | z8
0x19 | z9
0x1a | zX
0x1b | zE
   
0x20 - 0x2F | Vowels
--- | ---
0x20 | A
0x21 | Aa
0x22 | I
0x23 | U
0x24 | E
0x25 | Ey
0x26 | O
0x27 | Yy
0x28 | Uu
0x29 | Ii
0x2a | Oo
0x2b | Ao
0x2c | Oy

0x30 - 0x4F | Consonants
--- | ---
0x30 | F
0x31 | B
0x32 | P
0x33 | Y
0x34 | L
0x35 | R
0x36 | W
0x37 | M
0x38 | N
0x39 | H
0x3a | Z
0x3b | S
0x3c | Jh
0x3d | Sh
0x3e | Ch
0x3f | J
0x40 | D
0x41 | T
0x42 | Ng
0x43 | G
0x44 | K
0x45 | V
0x46 | Thh
0x47 | Th

0x50 - 0x5F | Punctuation
--- | ---
0x50 | FullStop
0x51 | PartialStop
0x52 | LeanMark
0x53 | LiterationMark
0x54 | IndefinitiveMark
0x55 | DefinitiveMark

## File Format
.vzk files simply consist of a series of bytes, each byte representing one
character based on the encoding listed above. However, the complexity is
moderately increased because of the way Vo'Zaken syllables are constructed.

One syllable in the language is composed of a vowel line with a series of
consonant glyphs located on top of the vowel line. Consonants placed on the
left side of the line are pronounced before the vowel sound, while consonants
on the right are pronounced after the vowel sound.

In .vzk files, this construction is achieved through a specific ordering to
all characters used in the syllable. For a vowel line `V` with pre-consonant
list `L` and post-consonant list `R`, the syllable is represented as `VL/R`,
where `/` is the line alternation character.

Either consonant list can be empty. Additionally, the line alternation
character can be excluded if the post-consonant list is empty. For instance,
"Vo'Zaken" can be represented as `<O><V><Lean><A><Z><E><K></><N>`. Note the
lack of line alternation character for the first two vowels.
