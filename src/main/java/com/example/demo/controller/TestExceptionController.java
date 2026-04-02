package com.example.demo.controller;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 测试异常控制器
 * 提供各种异常测试接口，用于测试工具捕获Java异常
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestExceptionController {

    /**
     * 测试空指针异常
     * GET /api/test/null-pointer?trigger=true
     */
    @GetMapping("/null-pointer")
    public ResponseEntity<Map<String, Object>> testNullPointerException(
            @RequestParam(required = false, defaultValue = "false") boolean trigger) {

        if (trigger) {
            // 故意触发空指针异常
            String nullString = null;
            int length = nullString.length(); // 这里会抛出NullPointerException
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "没有触发异常");
        return ResponseEntity.ok(response);
    }

    /**
     * 测试数组越界异常
     * GET /api/test/index-out-of-bounds?index=10
     */
    @GetMapping("/index-out-of-bounds")
    public ResponseEntity<Map<String, Object>> testIndexOutOfBoundsException(
            @RequestParam(defaultValue = "0") int index) {

        int[] array = {1, 2, 3, 4, 5};

        if (index >= array.length) {
            // 故意触发数组越界异常
            int value = array[index]; // 这里会抛出ArrayIndexOutOfBoundsException
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("value", array[index]);
        return ResponseEntity.ok(response);
    }

    /**
     * 测试算术异常（除零）
     * GET /api/test/arithmetic?divisor=0
     */
    @GetMapping("/arithmetic")
    public ResponseEntity<Map<String, Object>> testArithmeticException(
            @RequestParam(defaultValue = "1") int divisor) {

        int result = 100 / divisor; // 当divisor为0时抛出ArithmeticException

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("result", result);
        return ResponseEntity.ok(response);
    }

    /**
     * 测试数字格式异常
     * GET /api/test/number-format?number=abc
     */
    @GetMapping("/number-format")
    public ResponseEntity<Map<String, Object>> testNumberFormatException(
            @RequestParam String number) {

        try {
            int parsed = Integer.parseInt(number);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("parsed", parsed);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            throw new BusinessException("数字格式错误", "无法将 '" + number + "' 转换为数字");
        }
    }

    /**
     * 测试资源未找到异常
     * GET /api/test/resource-not-found?id=999
     */
    @GetMapping("/resource-not-found")
    public ResponseEntity<Map<String, Object>> testResourceNotFoundException(
            @RequestParam Long id) {

        if (id > 100) {
            throw new ResourceNotFoundException("测试资源", "id", id);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("id", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 测试业务异常
     * GET /api/test/business-error?trigger=true
     */
    @GetMapping("/business-error")
    public ResponseEntity<Map<String, Object>> testBusinessException(
            @RequestParam(defaultValue = "false") boolean trigger) {

        if (trigger) {
            throw new BusinessException("测试业务异常", "这是一个测试业务异常的消息");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "没有触发业务异常");
        return ResponseEntity.ok(response);
    }

    /**
     * 测试非法参数异常
     * GET /api/test/illegal-argument?value=-1
     */
    @GetMapping("/illegal-argument")
    public ResponseEntity<Map<String, Object>> testIllegalArgumentException(
            @RequestParam int value) {

        if (value < 0) {
            throw new IllegalArgumentException("参数值不能为负数: " + value);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }

    /**
     * 测试列表操作异常
     * GET /api/test/list-operation?operation=get&index=100
     */
    @GetMapping("/list-operation")
    public ResponseEntity<Map<String, Object>> testListOperationException(
            @RequestParam(defaultValue = "get") String operation,
            @RequestParam(defaultValue = "0") int index) {

        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        list.add("item3");

        Map<String, Object> response = new HashMap<>();

        switch (operation) {
            case "get":
                String item = list.get(index); // 可能抛出IndexOutOfBoundsException
                response.put("item", item);
                break;
            case "remove":
                list.remove(index); // 可能抛出IndexOutOfBoundsException
                response.put("message", "删除成功");
                break;
            default:
                response.put("list", list);
        }

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    /**
     * 测试Map操作异常
     * GET /api/test/map-operation?key=nonexistent
     */
    @GetMapping("/map-operation")
    public ResponseEntity<Map<String, Object>> testMapOperationException(
            @RequestParam String key) {

        Map<String, String> map = new HashMap<>();
        map.put("name", "旅行攻略");
        map.put("version", "1.0");

        // 尝试获取不存在的key的值并调用方法
        String value = map.get(key);
        if (value == null) {
            throw new ResourceNotFoundException("Map中的键", "key", key);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("value", value);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有可用的异常测试接口
     * GET /api/test
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTestEndpoints() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "异常测试接口列表");

        List<Map<String, String>> endpoints = new ArrayList<>();

        Map<String, String> e1 = new HashMap<>();
        e1.put("name", "空指针异常");
        e1.put("url", "/api/test/null-pointer?trigger=true");
        e1.put("description", "触发NullPointerException");
        endpoints.add(e1);

        Map<String, String> e2 = new HashMap<>();
        e2.put("name", "数组越界异常");
        e2.put("url", "/api/test/index-out-of-bounds?index=10");
        e2.put("description", "触发ArrayIndexOutOfBoundsException");
        endpoints.add(e2);

        Map<String, String> e3 = new HashMap<>();
        e3.put("name", "算术异常");
        e3.put("url", "/api/test/arithmetic?divisor=0");
        e3.put("description", "触发ArithmeticException (除零)");
        endpoints.add(e3);

        Map<String, String> e4 = new HashMap<>();
        e4.put("name", "数字格式异常");
        e4.put("url", "/api/test/number-format?number=abc");
        e4.put("description", "触发NumberFormatException");
        endpoints.add(e4);

        Map<String, String> e5 = new HashMap<>();
        e5.put("name", "资源未找到异常");
        e5.put("url", "/api/test/resource-not-found?id=999");
        e5.put("description", "触发ResourceNotFoundException");
        endpoints.add(e5);

        Map<String, String> e6 = new HashMap<>();
        e6.put("name", "业务异常");
        e6.put("url", "/api/test/business-error?trigger=true");
        e6.put("description", "触发BusinessException");
        endpoints.add(e6);

        Map<String, String> e7 = new HashMap<>();
        e7.put("name", "非法参数异常");
        e7.put("url", "/api/test/illegal-argument?value=-1");
        e7.put("description", "触发IllegalArgumentException");
        endpoints.add(e7);

        response.put("endpoints", endpoints);
        return ResponseEntity.ok(response);
    }
}
