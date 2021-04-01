# jstruct
Interpret bytes as packed binary data

##Install

### Gradle

```groovy
implemention 'com.lingyun.lib:jstruct:0.0.6'
```

### Maven

```xml
<dependency>
  <groupId>com.lingyun.lib</groupId>
  <artifactId>jstruct</artifactId>
  <version>0.0.6</version>
</dependency>
```

## Format Strings

Format strings are the mechanism used to specify the expected layout when packing and unpacking data. They are built up from [Format Characters](https://github.com/lingyun-x/jstruct#readme#format-characters), which specify the type of data being packed/unpacked. In addition, there are special characters for controlling the [Byte Order, Size, and Alignment](https://docs.python.org/3/library/struct.html#struct-alignment).



### Byte Order, Size, and Alignment

By default, C types are represented in the machine’s native format and byte order, and properly aligned by skipping pad bytes if necessary (according to the rules used by the C compiler).

Alternatively, the first character of the format string can be used to indicate the byte order, size and alignment of the packed data, according to the following table:

| Character | Byte order    | Size     | Alignment |
| :-------- | :------------ | :------- | :-------- |
| `<`       | little-endian | standard | none      |
| `>`       | big-endian    | standard | none      |

If the first character is not one of these, global byte order is assumed. you can set global byte order via `JStruct.byteOrder`

Native byte order is big-endian or little-endian, depending on the host system. For example, Intel x86 and AMD64 (x86-64) are little-endian; Motorola 68000 and PowerPC G5 are big-endian; ARM and Intel Itanium feature switchable endianness (bi-endian). Use `sys.byteorder` to check the endianness of your system.

Native size and alignment are determined using the C compiler’s `sizeof` expression. This is always combined with native byte order.

Standard size depends only on the format character; see the table in the [Format Characters](https://github.com/lingyun-x/jstruct#readme#format-characters) section.

There is no way to indicate non-native byte order (force byte-swapping); use the appropriate choice of `'<'` or `'>'`.


### Format Characters

Format characters have the following meaning; the conversion between C and Python values should be obvious given their types. The ‘Standard size’ column refers to the size of the packed value in bytes when using standard size; that is, when the format string starts with one of `'<'`, `'>'`, `'!'` or `'='`. When using native size, the size of the packed value is platform-dependent.

| Format | Java type   | Standard size | unit       |
| :----- | :---------- | :------------ | :--------- |
| `b`    | byte        | 1             | `int8`     |
| `B`    | short       | 1             | `uint8`    |
| `c`    | char        | 2             | `int16`    |
| `h`    | short       | 2             | `int16`    |
| `H`    | int         | 2             | `uint16`   |
| `i`    | int         | 4             | `int32`    |
| `I`    | long        | 4             | `uint32`   |
| `l`    | long        | 4             | `long`     |
| `f`    | float       | 4             | `float`    |
| `d`    | float       | 8             | `double`   |
| `[b]`  | ByteArray   | 1             | `int8[]`   |
| `[B]`  | ShortArray  | `1*N`         | `uint8[]`  |
| `[c]`  | CharArray   | `2*N`         | `int16[]`  |
| `[h]`  | ShortArray  | `2*N`         | `int16[]`  |
| `[H]`  | IntArray    | `2*N`         | `uint16[]` |
| `[i]`  | IntArray    | `4*N`         | `int32[]`  |
| `[I]`  | LongArray   | `4*N`         | `uint32[]` |
| `[l]`  | LongArray   | `4*N`         | `long[]`   |
| `[f]`  | FloatArray  | `4*N`         | `float[]`  |
| `[d]`  | DoubleArray | `8*N`         | `double[]` |
| s      | String      |               |            |

Note

1. A format character must be preceded by an integral repeat count. For example, the format string `'4h'`  exactly the same as `'hhhh'`.
2. Whitespace characters between formats are ignored; a count and its format must not contain whitespace though.
3. For the `'s'` or primitive array format character, the count is interpreted as the length of the bytes, not a repeat count like for the other format characters; for example, `'10s'` means a single 10-byte string, while `'10c'` means 10 characters,10[b] means a single 10-byte ByteArray while `10b` means 10 bytes . If a count is not given, it defaults to 1. For packing, the string is truncated or padded with null bytes as appropriate to make it fit. For unpacking, the resulting bytes object always has exactly the specified number of bytes. As a special case, `'0s'` means a single, empty string (while `'0c'` means 0 characters).
4. For the complex array format,the count is interpreted as the length of array.It will be parsed to a `List<Any>` type
5. For the `@` format character,the follow count is the index of already parse elements,if the count is negative it will be plus by current element index. For example `1i@0[b]` 

## Usage and Examples

### Primitive Data Type

```kotlin
val struct = "1b1B1h1H1i1I1l1f1d"
val aByte: Byte = 0x01
val aUByte: Short = 0xff
val aShort: Short = 0x0102.toShort()
val aUShort: Int = 0xff01
val aInt: Int = 0x01020304
val aUInt: Long = 0xf0020304
val along: Long = 0x01020304f1f2f3f4
val afloat: Float = 2.1f
val adouble: Double = 2.2
val elements = listOf<Any>(aByte, aUByte, aShort, aUShort, aInt, aUInt, along, afloat, adouble)
val bytes = JStruct.pack(struct, elements)
println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
//bytes: 01 FF 01 02 FF 01 01 02 03 04 F0 02 03 04 01 02 03 04 F1 F2 F3 F4 40 06 66 66 40 01 99 99 99 99 99 9A 
val unpackElements = JStruct.unpack(struct,bytes)
for (i in 0 until elements.size - 1) {
    assertEquals(elements[i], unpackElements[i])
}
```



### Primitive Array Type

#### Constant Array Length

```kotlin
val struct = "1i10[b]"

val len = 10
val bs = (0..9).toList().map { it.toByte() }.toByteArray()
val elements = listOf<Any>(len, bs)
val bytes = JStruct.pack(struct,elements)
println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
//bytes:00 00 00 0A 00 01 02 03 04 05 06 07 08 09 

val elements2 = JStruct.unpack(struct,result)
val aint = elements2.get(0) as Int
val abytearray = elements2.get(1) as ByteArray

assertEquals(aint, len)
println(abytearray.contentEquals(bs))
//true
```

#### Dynamic Array Length

```kotlin
val struct = "1i@0[b]"
//or
//val struct = "1i@-1[b]"

val len = 10
val bs = (0..9).toList().map { it.toByte() }.toByteArray()
val elements = listOf<Any>(len, bs)
val bytes = JStruct.pack(struct,elements)
println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
//bytes:00 00 00 0A 00 01 02 03 04 05 06 07 08 09 

val elements2 = JStruct.unpack(struct,result)
val aint = elements2.get(0) as Int
val abytearray = elements2.get(1) as ByteArray

assertEquals(aint, len)
println(abytearray.contentEquals(bs))
//true
```



### String Type

```kotlin
val struct = "1i10s"
val s = "0123456789"
val aint = s.toByteArray(JStruct.charset).size

val bytes = JStruct.pack(struct, listOf(aint, s))
println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
//bytes:00 00 00 0A 30 31 32 33 34 35 36 37 38 39 

val elements = JStruct.unpack(struct, bytes)
assertEquals(aint, elements[0])
assertEquals(s, elements[1])
```



### Complex Type

```kotlin
val struct ="1i2[1i10s]"
val s = "0123456789"
val slen :Int = s.toByteArray(JStruct.charset).size
val complex = listOf<Any>(slen,s)
val complexArray :List<Any> = listOf(complex,complex)

val clen :Int= 2
val elements:List<Any> = listOf(clen,complexArray)
val bytes = JStruct.pack(struct,elements)
println("bytes:${HexUtil.bytesToHexSpace(bytes)}")
//bytes:00 00 00 02 00 00 00 0A 30 31 32 33 34 35 36 37 38 39 00 00 00 0A 30 31 32 33 34 35 36 37 38 39 

val unpackElements  = JStruct.unpack(struct,bytes)

assertEquals(clen,unpackElements[0])

val unpackComplexArray = unpackElements[1] as List<Any>
val unpackComplex = unpackComplexArray[0] as List<Any>
        
for(i in complex.indices){
    assertEquals(complex[i],unpackComplex[i])
}
```

