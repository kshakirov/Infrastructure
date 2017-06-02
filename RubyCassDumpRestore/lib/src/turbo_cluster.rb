module TurboCassandra
  class TurboCluster
    @@session = nil
    @@hosts = ['10.1.3.15', '10.1.3.16', '10.1.3.17']
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