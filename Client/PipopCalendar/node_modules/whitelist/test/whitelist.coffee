Whitelist = require '../'

chai = require 'chai'
express = require 'express'
request = require 'supertest'

should = chai.should()

object =
  name: 'Moveline'
  email: 'support@moveline.com'
  _id: '12345'

expected =
  name: 'Moveline'
  email: 'support@moveline.com'

describe 'Whitelist', ->
  describe '#only', ->
    describe 'object', ->
      describe 'given an array of keys', ->
        it 'should return only whitelisted properties', ->
          Whitelist.only(object, ['name', 'email']).should.eql expected

      describe 'given an string of keys', ->
        it 'should return only whitelisted properties', ->
          Whitelist.only(object, 'name email').should.eql expected

    describe 'array', ->
      list = [
        { name: 'Moveline', email: 'support@moveline.com', _id: '12345' },
        { name: 'Christopher Garvis', email: 'cgarvis@gmail.com', _id: '54321' }
      ]

      list_expected = [
        { name: 'Moveline', email: 'support@moveline.com' },
        { name: 'Christopher Garvis', email: 'cgarvis@gmail.com' }
      ]

      describe 'given an array of keys', ->
        it 'should return only whitelisted properties', ->
          Whitelist.only(list, ['name', 'email']).should.eql list_expected

      describe 'given a string of keys', ->
        it 'should return only whitelisted properties', ->
          Whitelist.only(list, 'name email').should.eql list_expected

  describe '#middlware', ->
    app = express.createServer()
    app.use Whitelist.middleware('name email')

    app.all '/with-status-code', (req, res, next) ->
      res.json 200, object
    app.all '*', (req, res, next) ->
      res.json object

    describe 'sending only object', ->
      it 'should return only whitelisted properties', (done) ->
        request(app).get('/').end (err, res) ->
          res.body.should.eql expected
          done()

    describe 'sending status code and object', ->
      it 'should return only whitelisted properties', (done) ->
        request(app).get('/with-status-code').end (err, res) ->
          res.body.should.eql expected
          done()
