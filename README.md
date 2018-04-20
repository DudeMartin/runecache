# runecache
A small library for reading the contents of the cache for Old School RuneScape.

## Current features
* Landscapes
* Item definitions
* Sprites

### Example usage
A small test program that stores the *Smite* prayer icon in the current working directory.

```
public class ExampleUsage {

    public static void main(String[] args) throws Exception {
        Cache cache = new Cache(Cache.locateBaseDirectory());
        final int smiteSpriteId = 132;
        Sprite smiteSprite = Sprite.get(cache, smiteSpriteId);
        ImageIO.write(smiteSprite.frames[0], "png", new File("smite.png"));
    }
}
```

### Dependencies
* [Apache Commons Compress](https://commons.apache.org/proper/commons-compress/dependency-info.html)
* [org.json](https://mvnrepository.com/artifact/org.json/json)
