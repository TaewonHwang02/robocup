package common;

/**
 * An Enum to use in the state HashMaps. It is a way to prevent typos.
 */
public enum StateKeys {

    /**
     *VIEW SENSOR: field of view
     */
	// BALL
    ball_in_FOV, ball_centered_in_FOV, has_ball, carrying_ball_fwd,
    
    // OPPONENTS & TEAMATES
    oppo_goal_in_FOV, oppo_blocking, oppo_attacking, oppo_has_ball, dribble_mode, teammate_in_FOV, team_has_ball,
    
    // POSITIONING
    in_def_position, in_mid_position, in_att_position, in_win_position, aligned_with_ball, in_oppo_fourth, 

    // HEAR SENSOR: play modes (not all of them might be useful in the end)
    before_kick_off, play_on, time_up, time_over, kick_off_l, kick_off_r, kick_in_l, kick_in_r,
    free_kick_l, free_kick_r, corner_kick_l, corner_kick_r, goal_kick_l, goal_kick_r,
    goal_l, goal_r, drop_ball, offside_l, offside_r, penalty_kick_l, penalty_kick_r,
    foul_charge_l, foul_charge_r, back_pass_l, back_pass_r, free_kick_fault_l, free_kick_fault_r,
    indirect_free_kick_l, indirect_free_kick_r, illegal_defense_l, illegal_defense_r,

    // markers unrelated to world state
    kick_performed, 

    // miscellaneous
    move_allowed, in_new_position, designated_kick_off,

    //newly added

    in_side_defense, in_support_position, in_support_winger, in_support_midfielder;
}
