Gem::Specification.new do |s|
  s.name        = 'turbo_cassandra_dumper'
  s.version     = '1.0.1'
  s.date        = '2017-06-20'
  s.summary     = "TurboCassandraDumper!"
  s.description = "Tools to dump/restore Cassandra database"
  s.authors     = ["Kirill Shakirov"]
  s.email       = 'kshakirov@zoral.com.ua'
  s.files       = ["lib/turbo_cassandra_dumper.rb", "lib/turbo_cassandra_dumper/dumper.rb", "lib/turbo_cassandra_dumper/restorer.rb",
                   "lib/turbo_cassandra_dumper/keyspace_dumper.rb", "lib/turbo_cassandra_dumper/turbo_cluster.rb"]
  s.homepage    =
      'http://rubygems.org/gems/hola'
  s.license       = 'MIT'
  s.executables << 'dump.rb'
  s.executables << 'restore.rb'
  s.add_runtime_dependency 'cassandra-driver'
end