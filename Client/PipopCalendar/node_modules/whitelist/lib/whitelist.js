var Whitelist = {
  middleware: function middleware(keys) {
    return function(req, res, next) {
      var json = res.json;
      res.json = function(statusCode, object) {
        if('object' === typeof statusCode) {
          object = statusCode;
        }

        res.json = json;

        object = Whitelist.only(object, keys);

        res.json(object);
      };
      next();
    };
  },

  only: function only(object, keys) {
    object = object || {};
    if('string' === typeof keys) {
      keys = keys.split(/ +/);
    }

    var filter = function filter(obj, keys) {
      return keys.reduce(function(ret, key){
        ret[key] = obj[key];
        return ret;
      }, {});
    };

    if(Array.isArray(object)) {
      object = object.map(function(obj) { return filter(obj, keys); });
    } else {
      object = filter(object, keys);
    }

    return object;
  }
};

module.exports = Whitelist;
