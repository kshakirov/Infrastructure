require 'minitest/pride'
require_relative '../lib/source'


class TestServer < Minitest::Test
  def test_mai
    turbo_dns = TurboDns.new

    turbo_dns.return_address 'c1.turbocluster'
  end
end
