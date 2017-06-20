module TurboCassandra
  class TurboCluster
    @@session = nil
    @@hosts = ENV['TURBO_CASSANDRA_HOSTS'].split(',')
    def self.get_session keyspace
      if @@session.nil?
        @@session = connect_cluster keyspace
      else
        @@session
      end
    end

    def self.get_cassandra_seed
      @@hosts.first
    end
    def self.connect_cluster keyspace
      cluster =Cassandra.cluster(hosts: @@hosts)
      cluster.connect(keyspace)
    end
  end

end