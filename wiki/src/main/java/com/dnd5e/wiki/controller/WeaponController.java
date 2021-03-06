package com.dnd5e.wiki.controller;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dnd5e.wiki.model.creature.DamageType;
import com.dnd5e.wiki.model.creature.Dice;
import com.dnd5e.wiki.model.stock.Currency;
import com.dnd5e.wiki.model.stock.Weapon;
import com.dnd5e.wiki.model.stock.WeaponProperty;
import com.dnd5e.wiki.model.stock.WeaponType;
import com.dnd5e.wiki.repository.WeaponPropertyRepository;
import com.dnd5e.wiki.repository.WeaponRepository;

@Controller
@RequestMapping("/stock/weapons")
public class WeaponController {

	@Autowired
	private WeaponRepository repo;

	@Autowired
	private WeaponPropertyRepository propertyRepo;

	@GetMapping
	public String getWeapons(Model model) {
		List<Weapon> weapons = repo.findAll();
		Map<WeaponType, List<Weapon>> typyToWeapons = weapons.stream().collect(Collectors.groupingBy(Weapon::getType));
		model.addAttribute("weapons", typyToWeapons);
		Map<WeaponProperty, List<Weapon>> propertyByWeapons = weapons.stream()
				.flatMap(weapon -> weapon.getProperties().stream().map(property -> new SimpleEntry<>(property, weapon)))
                .collect(Collectors.groupingBy(Entry::getKey, Collectors.mapping(Entry::getValue, Collectors.toList())));
		
		Map<DamageType, List<Weapon>> damageTypeByWeapons = new EnumMap<>(DamageType.class); 
		weapons.forEach(w -> damageTypeByWeapons.computeIfAbsent(w.getDamageType(), a -> new ArrayList<>()).add(w));
		
		model.addAttribute("properties", propertyByWeapons);
		model.addAttribute("types", WeaponType.values());
		model.addAttribute("damageTypes", damageTypeByWeapons);
		model.addAttribute("currencies", Currency.values());
		return "hero/weapons";
	}

	@GetMapping("/property/{id}")
	public String getPropertyForm(Model model,  @PathVariable Integer id) {
		model.addAttribute("property", propertyRepo.findById(id).orElseGet(WeaponProperty::new));
		return "hero/weaponProperty";
	}

	@GetMapping("/add")
	public String getForm(Model model) {
		model.addAttribute("weapon", new Weapon());
		model.addAttribute("damageTypes", EnumSet.of(DamageType.PIERCING, DamageType.BLUDGEONING, DamageType.SLASHING, DamageType.NO_DAMAGE));
		model.addAttribute("dices", Dice.values());
		model.addAttribute("types", WeaponType.values());
		model.addAttribute("currencies", Currency.values());
		return "hero/addWeapon";
	}

	@PostMapping("/add")
	public String addWeapon(Weapon weapon) {
		repo.save(weapon);
		return "redirect:/weapons/add";
	}

	@GetMapping("/property/add")
	public String getPropertyForm(Model model) {
		model.addAttribute("weaponProperty", new WeaponProperty());
		return "hero/addWeaponProperty";
	}

	@PostMapping("/property/add")
	public String addWeaponProperty(WeaponProperty property) {
		propertyRepo.save(property);
		return "redirect:/weapons/property/add";
	}
}