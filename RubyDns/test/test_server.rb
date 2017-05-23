require 'minitest/autorun'
require 'minitest/pride'
require_relative '../lib/source'


class TestServer < Minitest::Test
  def setup
    @turbo_dns = TurboInternational::TurboDns.new
    @turbo_client = TurboInternational::Client.new
  end
  def test_resolve

    @turbo_dns.return_address 'c1.turbocluster'
  end
  def test_set_dns
    pattern = "set_dns:vpn.cluster.turbointernational.com:10.8.0.1"
    response = @turbo_dns.set_dns pattern
    assert_equal "10.8.0.1", response
    resolve = @turbo_dns.return_address 'vpn.cluster.turbointernational.com'
    assert_equal "", resolve
  end

  def test_client_addr
    ip = @turbo_client.get_vpn_ip
    assert_equal '192.168.42.1', ip
  end

  def test_client_set
    name = @turbo_client.set_dns 'c1'
    assert_equal 'kiril-shakirov-workstation', name
  end

  def test_query
    response  = @turbo_client.query 'c1'
    p response
  end

end
