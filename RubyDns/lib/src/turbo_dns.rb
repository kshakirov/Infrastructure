class TurboDns
  attr_reader :interfaces,:pattern,:upstream,:in
  def read_table
    YAML.load_file('data/table.yaml')
  end
  def initialize
    @pattern = /.turbocluster/
    @table = read_table

  end

  def return_address addr
    if @table.key? addr
      p addr
      p @table[addr]
      @table[addr]
    end
  end



end