import React from 'react';

export const inlineFormItemLayout = {
  labelCol: {
    sm: {
      span: 6,
    },
  },
  wrapperCol: {
    sm: {
      span: 18,
    },
  },
};

export default class CommonForm extends React.Component {
  get item() {
    const { propsAPI } = this.props;
    return propsAPI.getSelected()[0];
  }

  handleSubmit = e => {
    if (e && e.preventDefault) {
      e.preventDefault();
    }
    const { form, propsAPI } = this.props;
    const { getSelected, executeCommand, update } = propsAPI;
    setTimeout(() => {
      form.validateFieldsAndScroll((err, values) => {
        if (err) {
          return;
        }
        const item = getSelected()[0];
        if (!item) {
          return;
        }
        executeCommand(() => {
          update(item, { ...values });
        });
      });
    }, 0);
  };
}
