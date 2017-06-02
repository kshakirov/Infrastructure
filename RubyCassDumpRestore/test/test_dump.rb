require_relative 'test_helper'

class TestDumper < Minitest::Test
  def test_dump
    dumper = TurboCassandra::Dumper.new
    dumper.run 'turbo_test'
  end
end