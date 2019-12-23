/* eslint-disable no-param-reassign */
/* eslint-disable consistent-return */
import React, { useEffect, Fragment } from 'react';
import { Form, Input, Checkbox } from 'antd';
import { withPropsAPI } from 'gg-editor';
import { useImmer } from 'use-immer';
import { inlineFormItemLayout } from './CommonForm';

const { Item } = Form;

const EndNodeDetail = props => {
  const { propsAPI } = props;

  const [model, updateModel] = useImmer({
    hit: 0,
  });

  useEffect(() => {
    const { getSelected } = propsAPI;
    const item = getSelected()[0];
    updateModel(draft => {
      if (!item) {
        return;
      }
      draft = Object.assign({}, draft, item.getModel());
      return draft;
    });
  }, []);

  useEffect(() => {
    const { executeCommand, update, getSelected } = propsAPI;
    const item = getSelected()[0];
    executeCommand(() => {
      update(item, model);
    });
  }, [model]);

  return (
    <Fragment>
      <Item label="label" {...inlineFormItemLayout}>
        <Input
          value={model.label}
          onChange={e => {
            const { value } = e.target;
            updateModel(draft => {
              draft.label = value;
            });
          }}
        />
      </Item>
      <Item label="stream" {...inlineFormItemLayout}>
        <Input
          value={model.stream}
          onChange={e => {
            const { value } = e.target;
            updateModel(draft => {
              draft.stream = value;
            });
          }}
        />
      </Item>
      <Item label="hit" {...inlineFormItemLayout}>
        <Checkbox
          checked={model.hit === 1}
          onChange={e => {
            const { checked } = e.target;
            updateModel(draft => {
              draft.hit = +checked;
            });
          }}
        ></Checkbox>
      </Item>
    </Fragment>
  );
};

export default Form.create()(withPropsAPI(EndNodeDetail));
