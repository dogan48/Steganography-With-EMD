# Steganography With Exploiting Modification Direction

This project make image steganography with exploiting modification direction method.
Using Mod 5 and Mod 7.


# Getting Started

```
-git clone https://github.com/dogan48/Steganography-With-EMD.git
 and compile it.
 javac -cp ".:./commons-cli-1.4.jar" EmdApplication.java
 
```
After compiling

Options
```
 -c,--coverImage <arg>    Cover or Stego image file path.
 -m,--method <arg>        Enter method like embed or extract.
 -o,--outputImage <arg>   Output image file path.
 -s,--secretImage <arg>   Secret image file path.
```

Embedding: 

```

java -cp ".:./commons-cli-1.4.jar" EmdApplication -c ./coverImage.bmp -s ./secretImage.bmp -o ./stegoImage.bmp -m embed

```

Extracting: 

```

java -cp ".:./commons-cli-1.4.jar" EmdApplication  -c ./stegoImage.bmp -o ./secretImage.bmp -m extract

```
## Authors

* **Hadi Doğan Kocabıyık** [dogan48](https://github.com/dogan48)
* **Burak Aydın** [61baydin](https://github.com/61baydin)


## License

This project is licensed under the GNU License - see the [LICENSE.md](https://github.com/dogan48/Steganography-With-EMD/blob/master/LICENSE) file for details
