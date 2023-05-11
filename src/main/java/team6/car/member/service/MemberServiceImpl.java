package team6.car.member.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import team6.car.apartment.domain.Apartment;
import team6.car.member.DTO.MemberProfileDto;
import team6.car.member.DTO.ReportDto;
import team6.car.member.DTO.getReportDto;
import team6.car.member.domain.Complaint;
import team6.car.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team6.car.member.DTO.UserDto;
import team6.car.device.domain.Device;
import team6.car.member.repository.ComplaintRepository;
import team6.car.vehicle.domain.Vehicle;
import team6.car.apartment.repository.ApartmentRepository;
import team6.car.device.repository.DeviceRepository;
import team6.car.vehicle.repository.VehicleRepository;
import team6.car.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service //서비스 스프링 빈으로 등록
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final ApartmentRepository apartmentRepository;
    private final DeviceRepository deviceRepository;
    private final VehicleRepository vehicleRepository;
    private final ComplaintRepository complaintRepository;

    /**회원 가입**/
    @Override
    public Member register(UserDto userDto) throws Exception {
        /**
         * 이미 존재하는 이메일로 회원가입 요청 시 -> 예외 발생
         */
        if(memberRepository.findByEmail(userDto.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        //비밀번호 같은지 확인
        if(!Objects.equals(userDto.getPassword(), userDto.getPw_check())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check your password");
        }
        /**
         * 이미 존재하는 차량으로 회원가입 요청 시 -> 예외 발생
         */
        if(vehicleRepository.findByVehicleNumber(userDto.getVehicle_number()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle already exists");
        }

        //아파트 정보 저장
        Apartment apartment = new Apartment();
        apartment.setApartment_name(userDto.getApartment_name());
        apartmentRepository.save(apartment);

        //사용자 정보 저장
        Member member = new Member();
        member.setApartment(apartment); //FK 설정
        member.setName(userDto.getName());
        member.setPhone_number(userDto.getPhone_number());
        member.setEmail(userDto.getEmail());
        member.setPassword(userDto.getPassword());
        member.setAddress(userDto.getAddress());
        //validateDuplicateMember(member); //중복회원검사
        memberRepository.save(member);

        //디바이스 정보 저장
        Device device = new Device();
        device.setDevice_id(userDto.getDevice_id());
        deviceRepository.save(device);

        //차량 정보 저장
        Vehicle vehicle = new Vehicle();
        vehicle.setMember(member); //FK
        vehicle.setDevice(device); //FK
        vehicle.setVehicle_number(userDto.getVehicle_number());
        vehicle.setVehicle_model(userDto.getVehicle_model());
        vehicle.setVehicle_color(userDto.getVehicle_color());
        vehicleRepository.save(vehicle);

        return member;
    }

    /**로그인**/
    /*
    @Override
    public Member login(String email, String password) throws Exception {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));
        //비밀번호 같은지 확인
        if(!Objects.equals(password, member.getPassword())){
            throw new Exception("아이디 또는 비밀번호를 확인해주세요.");
        }
        // 사용자가 사는 아파트의 정보를 가져온다.
        Apartment apartment = apartmentRepository.findById(member.getApartment().getApartment_id())
                .orElseThrow(() -> new RuntimeException("아파트 정보를 찾지 못했습니다."));

        return member;
    }*/

    /**회원 id 로 정보 조회 (이메일, 차량 번호, 주소, 신고 횟수)**/
    @Override
    public List<MemberProfileDto> getMemberById(Long id) throws Exception{
        List<MemberProfileDto> memberProfileDto = new ArrayList<>();
        Member member = memberRepository.findById(id).orElseGet(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        });
        Vehicle vehicle = vehicleRepository.findByMemberId(id).orElseGet(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");
        });

        MemberProfileDto memberProfileDto1 = new MemberProfileDto(member.getEmail(), vehicle.getVehicle_number(), member.getAddress(), member.getNumber_of_complaints());
        memberProfileDto.add(memberProfileDto1);
        return memberProfileDto;
    }

    /**신고하기**/
    @Override
    public Complaint report(ReportDto reportDto) throws Exception{
        //차량 번호로 차량 정보 가져온 후 외래키로 있는 member의 id값 저장
        Vehicle vehicle = vehicleRepository.findByVehicleNumber(reportDto.getVehicle_number()).orElseGet(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found");
        });
        Long memberId = vehicle.getMember().getMember_id();
        //member id값 이용해 회원 정보 가져옴
        Member member = memberRepository.findById(memberId).orElseGet(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        });
        member = update(memberId, member.getName(), member.getPhone_number(), member.getEmail(), member.getPassword(), member.getAddress(), member.getNumber_of_complaints()+1, member.getApartment());
        memberRepository.deleteMember(memberId);
        memberRepository.save(member);

        //complaint_info에 저장
        Complaint complaint = new Complaint();
        complaint.setComplaint_contents(reportDto.getComplaint_contents());
        complaint.setMember(member);
        complaintRepository.save(complaint);

        return complaint;
    }

    public Member update(Long member_id, String name, String phone_number, String email, String password, String address, int number_of_complaints, Apartment apartment){
        Member member = new Member();
        member.setMember_id(member_id);
        member.setName(name);
        member.setPhone_number(phone_number);
        member.setEmail(email);
        member.setPassword(password);
        member.setAddress(address);
        member.setNumber_of_complaints(number_of_complaints);
        member.setApartment(apartment);
        return member;
    }

    /**신고 내용 조회**/
    @Override
    public List<getReportDto> getReportInfo(Long id) throws Exception {
        List<getReportDto> getReportDto1 = new ArrayList<>();
        Member member = memberRepository.findById(id).orElseGet(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        });
        Complaint complaint = complaintRepository.findComplaintByMemberId(id).orElseGet(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found");
        });

        getReportDto getReportDto2 = new getReportDto(complaint.getComplaint_contents(), member.getNumber_of_complaints());
        getReportDto1.add(getReportDto2);
        return getReportDto1;
    }
}
