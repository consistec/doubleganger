at first change to the path of the android sdk tools:
```bash
 cd /$ANDROID_HOME/platform-tools
```

open the adb shell:
```bash
adb shell
```

change to the path where your db file exists
```bash
cd /sdcard
```

with sqlite3 you can connect to your db fil
```bash
sqlite3 client.sl3
```

the command `.tables` lists you all tables included in the db file
```bash
sqlite> .tables
categories     categories_md
```

now you can use normal SQL commands to read your table content
```bash
sqlite> SELECT * FROM categories;
Soft drinks|Beverages|1
```

you can also update your db content
```bash
sqlite> UPDATE categories SET description="Hard drinks" WHERE id=1;

sqlite> SELECT * FROM categories;
Hard drinks|Beverages|1
```


