# Whitelist

Middlware for [Express](http://expressjs.com/)
for filters json responses.

## Installation

```bash
$ npm install whitelist
```

## Usage

```javascript
var express = require('express')
  , Whitelist = require('whitelist');

app = express.createServer();

app.get('/', Whitelist.middleware('name email'), function(req, res, next) {
  res.json({name: 'Moveline', email: 'support@moveline.com', id: '12345'});
});
```

## Test

```bash
$ npm test
```

## Authors [Christopher Garvis][0] & [Moveline][1]

[0]: http://christophergarvis.com
[1]: http://www.moveline.com

## License
MIT
