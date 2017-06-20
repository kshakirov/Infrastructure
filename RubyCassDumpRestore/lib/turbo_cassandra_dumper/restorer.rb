module TurboCassandra
  class Restorer

    def initialize
      @data_folder = ENV['TURBO_DUMP_FOLDER']
    end

    def from_fname_2_table_name filename
      table_name = filename.gsub('.csv', '')
      table_name.gsub(@data_folder + '/' + @keyspace + '/', '')
    end

    def from_csv filename
      table_name = from_fname_2_table_name filename
      cql = "\"USE #{@keyspace} ; COPY   #{table_name}  FROM '#{filename}';\""
      seed_host = TurboCassandra::TurboCluster.get_cassandra_seed
      command = "cqlsh #{seed_host}  --cqlversion=\"3.4.0\" -e #{cql}"
      puts command
    end

    def _run
      Dir.glob "#{@data_folder}/#{@keyspace}/*.csv" do |file|
        from_csv file
      end
    end

    def run keyspace
      @keyspace = keyspace
      _run
    end
  end
end