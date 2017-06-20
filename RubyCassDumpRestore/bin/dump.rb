#!/usr/bin/env ruby
require 'turbo_cassandra_dumper'
if ENV['TURBO_DUMP_FOLDER'] and ENV['TURBO_CASSANDRA_HOSTS']
  dumper = TurboCassandra::Dumper.new
  unless ARGV[0].nil?
    dumper.run ARGV[0]
  end
else
  puts "ADD ENV VARIABLES 'TURBO_DUMP_FOLDER' AND 'TURBO_CASSANDRA_HOSTS' AND TRY AGAIN"
end