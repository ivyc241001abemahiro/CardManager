package com.example.cardmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.cardmanager.R
import com.example.cardmanager.model.FaceInput


class FaceInputAdapter(
    private val list: MutableList<FaceInput>
) : RecyclerView.Adapter<FaceInputAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        val txtFace =
            view.findViewById<TextView>(
                R.id.txtFaceNumber
            )


        val editName =
            view.findViewById<EditText>(
                R.id.edtFaceName
            )


        val editCost =
            view.findViewById<EditText>(
                R.id.edtCost
            )


        val spinnerType =
            view.findViewById<Spinner>(
                R.id.spinnerType
            )


        val editRace =
            view.findViewById<EditText>(
                R.id.edtRace
            )


        val editPower =
            view.findViewById<EditText>(
                R.id.edtPower
            )


        val editAbility =
            view.findViewById<EditText>(
                R.id.edtAbility
            )


        // 文明チェックボックス
        val checkLight =
            view.findViewById<CheckBox>(
                R.id.checkLight
            )

        val checkWater =
            view.findViewById<CheckBox>(
                R.id.checkWater
            )

        val checkDark =
            view.findViewById<CheckBox>(
                R.id.checkDark
            )

        val checkFire =
            view.findViewById<CheckBox>(
                R.id.checkFire
            )

        val checkNature =
            view.findViewById<CheckBox>(
                R.id.checkNature
            )

        val checkZero =
            view.findViewById<CheckBox>(
                R.id.checkZero
            )

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {


        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_face_input,
                    parent,
                    false
                )


        return ViewHolder(view)
    }



    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {


        val face = list[position]


        holder.txtFace.text =
            "面${position + 1}"


        holder.editName.setText(
            face.name
        )


        holder.editCost.setText(
            if(face.cost == 0)
                ""
            else
                face.cost.toString()
        )


        holder.editRace.setText(
            face.race
        )


        holder.editPower.setText(
            face.power?.toString() ?: ""
        )


        holder.editAbility.setText(
            face.ability
        )



        // 文明復元
        holder.checkLight.isChecked =
            face.civilizations.contains("光")

        holder.checkWater.isChecked =
            face.civilizations.contains("水")

        holder.checkDark.isChecked =
            face.civilizations.contains("闇")

        holder.checkFire.isChecked =
            face.civilizations.contains("火")

        holder.checkNature.isChecked =
            face.civilizations.contains("自然")

        holder.checkZero.isChecked =
            face.civilizations.contains("無色")



        // 名前
        holder.editName.doAfterTextChanged {

            face.name =
                it.toString()

        }


        // コスト
        holder.editCost.doAfterTextChanged {

            face.cost =
                it.toString()
                    .toIntOrNull()
                    ?: 0

        }


        // 種族
        holder.editRace.doAfterTextChanged {

            face.race =
                it.toString()

        }


        // パワー
        holder.editPower.doAfterTextChanged {

            face.power =
                it.toString()
                    .toIntOrNull()

        }


        // 能力
        holder.editAbility.doAfterTextChanged {

            face.ability =
                it.toString()

        }


        // 文明変更
        val updateCivilization = {

            val civilizations =
                mutableListOf<String>()


            if(holder.checkLight.isChecked)
                civilizations.add("光")


            if(holder.checkWater.isChecked)
                civilizations.add("水")


            if(holder.checkDark.isChecked)
                civilizations.add("闇")


            if(holder.checkFire.isChecked)
                civilizations.add("火")


            if(holder.checkNature.isChecked)
                civilizations.add("自然")


            if(holder.checkZero.isChecked)
                civilizations.add("無色")


            face.civilizations =
                civilizations
        }


        holder.checkLight.setOnCheckedChangeListener { _, _ ->
            updateCivilization()
        }

        holder.checkWater.setOnCheckedChangeListener { _, _ ->
            updateCivilization()
        }

        holder.checkDark.setOnCheckedChangeListener { _, _ ->
            updateCivilization()
        }

        holder.checkFire.setOnCheckedChangeListener { _, _ ->
            updateCivilization()
        }

        holder.checkNature.setOnCheckedChangeListener { _, _ ->
            updateCivilization()
        }

        holder.checkZero.setOnCheckedChangeListener { _, _ ->
            updateCivilization()
        }


        // タイプ
        holder.spinnerType.setSelection(
            when(face.type){

                "クリーチャー" -> 1
                "呪文" -> 2
                "タマシード" -> 3
                "クロスギア" -> 4
                "ドラグハート" -> 5
                "サイキック" -> 6
                "フィールド" -> 7
                "特殊" -> 8

                else -> 0
            }
        )

        // タイプ変更
        holder.spinnerType.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    face.type =
                        when (position) {
                            1 -> "呪文"
                            2 -> "タマシード"
                            3 -> "クロスギア"
                            4 -> "ドラグハート"
                            5 -> "サイキック"
                            6 -> "フィールド"
                            else -> ""
                        }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                    face.type = ""
                }
            }
    }



    override fun getItemCount(): Int {

        return list.size

    }

}