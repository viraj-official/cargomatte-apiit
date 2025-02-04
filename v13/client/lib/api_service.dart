import 'dart:convert';
import 'package:http/http.dart' as http;
import 'constants.dart';

class ApiService {
  final String bearerToken;

  ApiService(this.bearerToken);

  Future<List<dynamic>> fetchOrders() async {
    final response = await http.get(
      Uri.parse(Constants.orderEndpoint),
      headers: {
        "Authorization": "Bearer $bearerToken",
        "Content-Type": "application/json",
      },
    );

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Failed to load orders');
    }
  }

  Future<Map<String, dynamic>> searchOrder(String trackingId) async {
    final response = await http.get(
      Uri.parse("${Constants.trackOrderEndpoint}/$trackingId"),
      headers: {
        "Authorization": "Bearer $bearerToken",
        "Content-Type": "application/json",
      },
    );

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Order not found');
    }
  }

  Future<void> updateOrder(int orderId, String newAddress, String newDeliveryDate) async {
    final response = await http.put(
      Uri.parse("${Constants.orderEndpoint}/$orderId"),
      headers: {
        "Authorization": "Bearer $bearerToken",
        "Content-Type": "application/json",
      },
      body: jsonEncode({
        "shipmentAddress": newAddress,
        "deliveryDate": newDeliveryDate,
      }),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to update order');
    }
  }

  Future<bool> createOrder(Map<String, dynamic> orderRequest) async {
    final response = await http.post(
      Uri.parse(Constants.createOrderEndpoint),
      headers: {
        "Authorization": "Bearer $bearerToken",
        "Content-Type": "application/json",
      },
      body: jsonEncode(orderRequest),
    );

    return response.statusCode == 200 || response.statusCode == 201;
  }
}