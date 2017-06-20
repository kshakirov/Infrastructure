module TurboCassandra
  class Dumper
    private

    def check_data_dirs
      unless Dir.exists? @data_folder
        Dir.mkdir @data_folder
      end
      unless Dir.exists?  "#{@data_folder}/#{@keyspace}"
        Dir.mkdir "#{@data_folder}/#{@keyspace}"
      end
    end

    def init keyspace
      @session = TurboCassandra::TurboCluster.get_session keyspace
      @keyspace = keyspace
      @data_folder = ENV['TURBO_DUMP_FOLDER']
      check_data_dirs
    end

    def execute cql, args
      statement = @session.prepare(cql)
      @session.execute(statement, arguments: args, consistency: :one)
    end

    def to_csv table_name
      cql = "\"USE #{@keyspace} ; COPY   #{table_name}  TO '#{@data_folder}/#{@keyspace}/#{table_name}.csv';\""
      seed_host = TurboCassandra::TurboCluster.get_cassandra_seed
      command = "cqlsh #{seed_host}  --cqlversion=\"3.4.0\" -e #{cql}"
      output = system(command)
      puts output
    end

    def list_tables
      cql = "SELECT table_name from system_schema.tables  WHERE keyspace_name='#{@keyspace}'"
      execute cql, []
    end

    def _run
      list_tables.each do |table|
        to_csv table['table_name']
      end
    end

    public
    def run keyspace
      init keyspace
      _run
    end
  end
end