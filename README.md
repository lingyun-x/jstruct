# jstruct
Interpret bytes as packed binary data

## Format Strings

Format strings are the mechanism used to specify the expected layout when packing and unpacking data. They are built up from [Format Characters](https://docs.python.org/3/library/struct.html#format-characters), which specify the type of data being packed/unpacked. In addition, there are special characters for controlling the [Byte Order, Size, and Alignment](https://docs.python.org/3/library/struct.html#struct-alignment).



### Byte Order, Size, and Alignment

By default, C types are represented in the machine’s native format and byte order, and properly aligned by skipping pad bytes if necessary (according to the rules used by the C compiler).

Alternatively, the first character of the format string can be used to indicate the byte order, size and alignment of the packed data, according to the following table:

| Character | Byte order    | Size     | Alignment |
| :-------- | :------------ | :------- | :-------- |
|           |               |          |           |
|           |               |          |           |
| `<`       | little-endian | standard | none      |
| `>`       | big-endian    | standard | none      |
|           |               |          |           |

If the first character is not one of these, `'@'` is assumed.

Native byte order is big-endian or little-endian, depending on the host system. For example, Intel x86 and AMD64 (x86-64) are little-endian; Motorola 68000 and PowerPC G5 are big-endian; ARM and Intel Itanium feature switchable endianness (bi-endian). Use `sys.byteorder` to check the endianness of your system.

Native size and alignment are determined using the C compiler’s `sizeof` expression. This is always combined with native byte order.

Standard size depends only on the format character; see the table in the [Format Characters](https://docs.python.org/3/library/struct.html#format-characters) section.

Note the difference between `'@'` and `'='`: both use native byte order, but the size and alignment of the latter is standardized.

The form `'!'` represents the network byte order which is always big-endian as defined in [IETF RFC 1700](https://tools.ietf.org/html/rfc1700).

There is no way to indicate non-native byte order (force byte-swapping); use the appropriate choice of `'<'` or `'>'`.

Notes:

1. Padding is only automatically added between successive structure members. No padding is added at the beginning or the end of the encoded struct.
2. No padding is added when using non-native size and alignment, e.g. with ‘<’, ‘>’, ‘=’, and ‘!’.
3. To align the end of a structure to the alignment requirement of a particular type, end the format with the code for that type with a repeat count of zero. See [Examples](https://docs.python.org/3/library/struct.html#struct-examples).



### Format Characters

Format characters have the following meaning; the conversion between C and Python values should be obvious given their types. The ‘Standard size’ column refers to the size of the packed value in bytes when using standard size; that is, when the format string starts with one of `'<'`, `'>'`, `'!'` or `'='`. When using native size, the size of the packed value is platform-dependent.

| Format | Java type   | Standard size | unit       |
| :----- | :---------- | :------------ | :--------- |
| `b`    | byte        | 1             | `int8`     |
| `B`    | short       | 1             | `uint8`    |
| `h`    | short       | 2             | `int16`    |
| `H`    | int         | 2             | `uint16`   |
| `i`    | int         | 4             | `int32`    |
| `I`    | long        | 4             | `uint32`   |
| `l`    | long        | 4             | `long`     |
| `f`    | float       | 4             | `float`    |
| `d`    | float       | 8             | `double`   |
| `[b]`  | ByteArray   | 1             | `int8*N`   |
| `[B]`  | ShortArray  | `1*N`         | `uint8*N`  |
| `[h]`  | ShortArray  | `2*N`         | `int16*N`  |
| `[H]`  | IntArray    | `2*N`         | `uint16*N` |
| `[i]`  | IntArray    | `4*N`         | `int32*N`  |
| `[I]`  | LongArray   | `4*N`         | `uint32*N` |
| `[l]`  | LongArray   | `4*N`         | `long*N`   |
| `[f]`  | FloatArray  | `4*N`         | `float*N`  |
| `[d]`  | DoubleArray | `8*N`         | `double*N` |
| `[*]`  | Complex     |               |            |

