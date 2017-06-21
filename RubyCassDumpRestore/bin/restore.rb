#!/usr/bin/env ruby
require 'turbo_cassandra_dumper'

if ARGV[0].nil?
  puts "Supply An argument - keyspace to restore"
  exit 1
end

restorer = TurboCassandra::Restorer.new
unless ARGV[0].nil?
  restorer.run ARGV[0]
end