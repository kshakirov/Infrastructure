require_relative '../lib/source'
restorer = TurboCassandra::Restorer.new
unless ARGV[0].nil?
  restorer.run ARGV[0]
end