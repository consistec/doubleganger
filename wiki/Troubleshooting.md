FAQ
===

I'm getting "error: Failed connect to github.com:443; Operation now in progress while accessing" while cloning submodule. What am I missing?
-----------------

Try setting your proxy via:
```bash
$ git config --global http.proxy proxy.sb.consistec.de:3128
```
