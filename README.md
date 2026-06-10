Dựa vào quy tắc nghiệp vụ và mã nguồn được cung cấp, phương thức updateStock đang gặp 2 lỗi logic chính:

Lỗi vi phạm quy tắc cập nhật CSDL:

Nguyên nhân: Mã nguồn hiện tại đang comment dòng // productRepository.save(product);.

Hậu quả: Sự thay đổi số lượng tồn kho chỉ được lưu trên RAM (biến product cục bộ) chứ không được ghi nhận xuống cơ sở dữ liệu. Lần gọi API tiếp theo sẽ vẫn trả về số lượng cũ.

Lỗi ném sai loại Exception (Chưa tuân thủ chuẩn):

Nguyên nhân: Khi newStock < 0, hệ thống đang ném ra IllegalStateException.

Cách khắc phục: Theo gợi ý trong code, đây là lỗi do tham số đầu vào (cố tình trừ đi lượng quá lớn so với tồn kho thực tế), nên việc ném ra IllegalArgumentException sẽ phản ánh đúng nghiệp vụ hơn."# SS11_EX_2" 
