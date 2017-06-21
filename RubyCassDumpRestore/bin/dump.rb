#!/usr/bin/env ruby
require 'turbo_cassandra_dumper'

if ENV['TURBO_DUMP_FOLDER'].nil? or ENV['TURBO_CASSANDRA_HOSTS'].nil?
  puts "ADD ENV VARIABLES 'TURBO_DUMP_FOLDER' AND 'TURBO_CASSANDRA_HOSTS' AND TRY AGAIN"
  exit 1
end

if ARGV[0].nil?
  puts "Supply An argument - keyspace to dump"
  exit 1
end


timers = Timers::Group.new
dumper = TurboCassandra::Dumper.new

now_and_every_five_seconds = timers.now_and_every(12 * 60 * 60) do
  puts " ################# STARTING DUMP  ########"
  dumper.run ARGV[0]
  puts " ################# ENDING  DUMP ########"
end

loop {timers.wait}




