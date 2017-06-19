require_relative '../lib/turbo_cassandra_dumper'
restorer = TurboCassandra::Restorer.new
unless ARGV[0].nil?
  restorer.run ARGV[0]
end