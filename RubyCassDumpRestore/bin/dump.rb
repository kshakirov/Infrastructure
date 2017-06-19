#!/usr/bin/env ruby
require 'turbo_cassandra_dumper'
dumper = TurboCassandra::Dumper.new
unless ARGV[0].nil?
  dumper.run ARGV[0]
end