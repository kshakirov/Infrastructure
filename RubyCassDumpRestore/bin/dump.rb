require_relative '../lib/source'
dumper = TurboCassandra::Dumper.new
unless ARGV[0].nil?
  dumper.run ARGV[0]
end